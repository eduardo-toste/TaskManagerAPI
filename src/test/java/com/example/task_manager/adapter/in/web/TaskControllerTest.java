package com.example.task_manager.adapter.in.web;

import com.example.task_manager.adapter.in.web.handler.ErrorResponseFactory;
import com.example.task_manager.adapter.in.web.handler.GlobalExceptionHandler;
import com.example.task_manager.adapter.in.web.mapper.TaskWebMapper;
import com.example.task_manager.adapter.in.web.request.CreateTaskRequest;
import com.example.task_manager.adapter.in.web.response.TaskResponse;
import com.example.task_manager.application.dto.CreateTaskCommand;
import com.example.task_manager.application.dto.TaskOutput;
import com.example.task_manager.application.exception.TaskNotFoundException;
import com.example.task_manager.application.port.in.CompleteTaskUseCase;
import com.example.task_manager.application.port.in.CreateTaskUseCase;
import com.example.task_manager.application.port.in.FindTaskUseCase;
import com.example.task_manager.domain.enums.TaskStatus;
import com.example.task_manager.domain.exception.TaskAlreadyCompletedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@Import({GlobalExceptionHandler.class, ErrorResponseFactory.class})
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateTaskUseCase createTaskUseCase;

    @MockitoBean
    private FindTaskUseCase findTaskUseCase;

    @MockitoBean
    private CompleteTaskUseCase completeTaskUseCase;

    @MockitoBean
    private TaskWebMapper taskWebMapper;

    @Test
    @DisplayName("Should create task successfully")
    void shouldCreateTaskSuccessfully() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest("Study", "Hexagonal architecture");
        CreateTaskCommand command = new CreateTaskCommand("Study", "Hexagonal architecture");
        TaskOutput output = new TaskOutput(1L, "Study", "Hexagonal architecture", TaskStatus.PENDING);
        TaskResponse response = new TaskResponse(1L, "Study", "Hexagonal architecture", TaskStatus.PENDING);

        when(taskWebMapper.toCommand(any(CreateTaskRequest.class))).thenReturn(command);
        when(createTaskUseCase.createTask(command)).thenReturn(output);
        when(taskWebMapper.toResponse(output)).thenReturn(response);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Study"))
                .andExpect(jsonPath("$.description").value("Hexagonal architecture"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("Should return 400 when create request is invalid")
    void shouldReturn400WhenCreateRequestIsInvalid() throws Exception {
        String invalidJson = """
                {
                  "title": "",
                  "description": "Hexagonal architecture"
                }
                """;

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.path").value("/tasks"));
    }

    @Test
    @DisplayName("Should return paged tasks successfully")
    void shouldReturnPagedTasksSuccessfully() throws Exception {
        TaskOutput output1 = new TaskOutput(1L, "Study", "Hexagonal architecture", TaskStatus.PENDING);
        TaskOutput output2 = new TaskOutput(2L, "Test", "Controller test", TaskStatus.COMPLETED);

        TaskResponse response1 = new TaskResponse(1L, "Study", "Hexagonal architecture", TaskStatus.PENDING);
        TaskResponse response2 = new TaskResponse(2L, "Test", "Controller test", TaskStatus.COMPLETED);

        Page<TaskOutput> outputPage = new PageImpl<>(
                List.of(output1, output2),
                PageRequest.of(0, 10),
                2
        );

        Page<TaskResponse> responsePage = new PageImpl<>(
                List.of(response1, response2),
                PageRequest.of(0, 10),
                2
        );

        when(findTaskUseCase.findTasks(argThat(pageable ->
                pageable.getPageNumber() == 0 && pageable.getPageSize() == 10
        ))).thenReturn(outputPage);

        when(taskWebMapper.toResponse(outputPage)).thenReturn(responsePage);

        mockMvc.perform(get("/tasks")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Study"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].status").value("COMPLETED"))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    @DisplayName("Should return task by id successfully")
    void shouldReturnTaskByIdSuccessfully() throws Exception {
        TaskOutput output = new TaskOutput(1L, "Study", "Hexagonal architecture", TaskStatus.PENDING);
        TaskResponse response = new TaskResponse(1L, "Study", "Hexagonal architecture", TaskStatus.PENDING);

        when(findTaskUseCase.getTaskById(1L)).thenReturn(output);
        when(taskWebMapper.toResponse(output)).thenReturn(response);

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Study"))
                .andExpect(jsonPath("$.description").value("Hexagonal architecture"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("Should return 404 when task by id is not found")
    void shouldReturn404WhenTaskByIdIsNotFound() throws Exception {
        when(findTaskUseCase.getTaskById(99L)).thenThrow(new TaskNotFoundException());

        mockMvc.perform(get("/tasks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.path").value("/tasks/99"));
    }

    @Test
    @DisplayName("Should complete task successfully")
    void shouldCompleteTaskSuccessfully() throws Exception {
        TaskOutput output = new TaskOutput(1L, "Study", "Hexagonal architecture", TaskStatus.COMPLETED);
        TaskResponse response = new TaskResponse(1L, "Study", "Hexagonal architecture", TaskStatus.COMPLETED);

        when(completeTaskUseCase.completeTask(1L)).thenReturn(output);
        when(taskWebMapper.toResponse(output)).thenReturn(response);

        mockMvc.perform(patch("/tasks/1/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("Should return 409 when task is already completed")
    void shouldReturn409WhenTaskIsAlreadyCompleted() throws Exception {
        when(completeTaskUseCase.completeTask(1L))
                .thenThrow(new TaskAlreadyCompletedException());

        mockMvc.perform(patch("/tasks/1/complete"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.path").value("/tasks/1/complete"));
    }
}