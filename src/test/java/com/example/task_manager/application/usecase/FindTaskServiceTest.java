package com.example.task_manager.application.usecase;

import com.example.task_manager.application.dto.TaskOutput;
import com.example.task_manager.application.exception.TaskNotFoundException;
import com.example.task_manager.application.mapper.TaskApplicationMapper;
import com.example.task_manager.application.port.out.TaskRepositoryPort;
import com.example.task_manager.domain.enums.TaskStatus;
import com.example.task_manager.domain.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindTaskServiceTest {

    @Mock
    private TaskRepositoryPort taskRepositoryPort;

    @Mock
    private TaskApplicationMapper taskApplicationMapper;

    @InjectMocks
    private FindTaskService findTaskService;

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task(
                null,
                "Titulo Teste",
                "Descricao Teste"
        );
    }

    @Test
    void shouldReturnTasksPage() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        TaskOutput taskOutput = new TaskOutput(1L, "Titulo Teste", "Descricao Teste", TaskStatus.PENDING);
        Page<Task> taskPage = new PageImpl<>(List.of(task));
        Page<TaskOutput> taskOutputPage = new PageImpl<>(List.of(taskOutput), pageable, 1);
        when(taskRepositoryPort.findTasks(pageable)).thenReturn(taskPage);
        when(taskApplicationMapper.toTaskOutputPage(taskPage)).thenReturn(taskOutputPage);

        // Act
        Page<TaskOutput> taskOutputPageResult = findTaskService.findTasks(pageable);

        // Assert
        assertEquals(1, taskOutputPageResult.getTotalElements());
        assertEquals(1L, taskOutputPageResult.getContent().get(0).id());
        assertEquals("Titulo Teste", taskOutputPageResult.getContent().get(0).title());
        assertEquals("Descricao Teste", taskOutputPageResult.getContent().get(0).description());
        assertEquals(TaskStatus.PENDING, taskOutputPageResult.getContent().get(0).status());

        verify(taskRepositoryPort).findTasks(pageable);
        verify(taskApplicationMapper).toTaskOutputPage(taskPage);
    }

    @Test
    void shouldReturnEmptyPageWhenNoTasksExist() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> emptyPage = Page.empty(pageable);
        Page<TaskOutput> emptyOutputPage = Page.empty(pageable);
        when(taskRepositoryPort.findTasks(pageable)).thenReturn(emptyPage);
        when(taskApplicationMapper.toTaskOutputPage(emptyPage)).thenReturn(emptyOutputPage);

        // Act
        Page<TaskOutput> result = findTaskService.findTasks(pageable);

        // Assert
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    void shouldReturnTaskByIdSuccessfully() {
        // Arrange
        Long id = 1L;
        TaskOutput taskOutput = new TaskOutput(
                1L,
                "Titulo Teste",
                "Descricao Teste",
                TaskStatus.PENDING
        );
        when(taskRepositoryPort.findById(id)).thenReturn(Optional.of(task));
        when(taskApplicationMapper.toTaskOutput(task)).thenReturn(taskOutput);

        // Act
        TaskOutput result = findTaskService.getTaskById(id);

        // Assert
        assertEquals(1L, result.id());
        assertEquals(task.getTitle(), result.title());
        assertEquals(task.getDescription(), result.description());
        assertEquals(TaskStatus.PENDING, result.status());

        verify(taskRepositoryPort).findById(id);
        verify(taskApplicationMapper).toTaskOutput(task);
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFound() {
        // Arrange
        Long id = 1L;
        when(taskRepositoryPort.findById(id)).thenReturn(Optional.empty());

        // Act
        var ex = assertThrows(TaskNotFoundException.class,
                () -> findTaskService.getTaskById(id));

        // Assert
        assertEquals("Task not found.", ex.getMessage());
    }

}