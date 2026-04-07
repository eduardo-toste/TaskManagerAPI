package com.example.task_manager.application.usecase;

import com.example.task_manager.application.dto.CreateTaskCommand;
import com.example.task_manager.application.dto.TaskOutput;
import com.example.task_manager.application.mapper.TaskApplicationMapper;
import com.example.task_manager.application.port.out.TaskRepositoryPort;
import com.example.task_manager.domain.model.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateTaskServiceTest {

    @Mock
    private TaskRepositoryPort taskRepositoryPort;

    @Mock
    private TaskApplicationMapper taskApplicationMapper;

    @InjectMocks
    private CreateTaskService createTaskService;

    @Test
    void shouldCreateTaskSuccessfully() {
        // Arrange
        CreateTaskCommand command = new CreateTaskCommand("Study", "Hexagonal architecture");
        Task savedTask = new Task(1L, "Study", "Hexagonal architecture");
        TaskOutput expectedOutput = new TaskOutput(
                1L,
                "Study",
                "Hexagonal architecture",
                savedTask.getStatus()
        );
        when(taskRepositoryPort.save(any(Task.class))).thenReturn(savedTask);
        when(taskApplicationMapper.toTaskOutput(any())).thenReturn(expectedOutput);

        // Act
        TaskOutput result = createTaskService.createTask(command);

        // Assert
        assertEquals(1L, result.id());
        assertEquals("Study", result.title());
        assertEquals("Hexagonal architecture", result.description());

        verify(taskRepositoryPort).save(any(Task.class));
        verify(taskApplicationMapper).toTaskOutput(any());
    }

}