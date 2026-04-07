package com.example.task_manager.application.usecase;

import com.example.task_manager.application.dto.TaskOutput;
import com.example.task_manager.application.exception.TaskNotFoundException;
import com.example.task_manager.application.mapper.TaskApplicationMapper;
import com.example.task_manager.application.port.out.TaskRepositoryPort;
import com.example.task_manager.domain.enums.TaskStatus;
import com.example.task_manager.domain.exception.TaskAlreadyCompletedException;
import com.example.task_manager.domain.model.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompleteTaskServiceTest {

    @Mock
    private TaskRepositoryPort taskRepositoryPort;

    @Mock
    private TaskApplicationMapper taskApplicationMapper;

    @InjectMocks
    private CompleteTaskService completeTaskService;

    @Test
    void shouldCompleteTaskSuccessfully() {
        // Arrange
        Long taskId = 1L;
        Task savedTask = new Task(1L, "Study", "Hexagonal architecture");
        TaskOutput expectedOutput = new TaskOutput(
                1L,
                "Study",
                "Hexagonal architecture",
                savedTask.getStatus()
        );
        when(taskRepositoryPort.findById(taskId)).thenReturn(Optional.of(savedTask));
        when(taskRepositoryPort.save(any(Task.class))).thenReturn(savedTask);
        when(taskApplicationMapper.toTaskOutput(any(Task.class))).thenReturn(expectedOutput);

        // Act
        TaskOutput result = completeTaskService.completeTask(taskId);

        // Assert
        assertEquals(1L, result.id());
        assertEquals("Study", result.title());
        assertEquals("Hexagonal architecture", result.description());

        verify(taskRepositoryPort).findById(taskId);
        verify(taskRepositoryPort).save(any(Task.class));
        verify(taskApplicationMapper).toTaskOutput(any(Task.class));
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFound() {
        // Arrange
        Long taskId = 1L;
        when(taskRepositoryPort.findById(taskId)).thenReturn(Optional.empty());

        // Act
        var ex = assertThrows(TaskNotFoundException.class,
                () -> completeTaskService.completeTask(taskId));

        // Assert
        assertEquals("Task not found.", ex.getMessage());

        verify(taskRepositoryPort, never()).save(any(Task.class));
        verify(taskApplicationMapper, never()).toTaskOutput(any(Task.class));
    }

    @Test
    void shouldThrowExceptionWhenTaskIsAlreadyComplete() {
        // Arrange
        Long taskId = 1L;
        Task savedTask = new Task(1L, "Study", "Hexagonal architecture", TaskStatus.COMPLETED);
        when(taskRepositoryPort.findById(taskId)).thenReturn(Optional.of(savedTask));

        // Act
        var ex = assertThrows(TaskAlreadyCompletedException.class,
                () -> completeTaskService.completeTask(taskId));

        // Assert
        assertEquals("Task is already completed.", ex.getMessage());

        verify(taskRepositoryPort, never()).save(any(Task.class));
        verify(taskApplicationMapper, never()).toTaskOutput(any(Task.class));
    }

}