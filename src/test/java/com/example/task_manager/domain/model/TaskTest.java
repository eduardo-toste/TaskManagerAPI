package com.example.task_manager.domain.model;

import com.example.task_manager.domain.enums.TaskStatus;
import com.example.task_manager.domain.exception.TaskAlreadyCompletedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task(null, "Test Task", "Test Description");
    }

    @Test
    void shouldCreateTaskWithPendingStatus() {
        assertEquals(TaskStatus.PENDING, task.getStatus());
    }

    @Test
    void shouldCompleteTaskSuccessfully() {
        task.complete();

        assertEquals(TaskStatus.COMPLETED, task.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenTaskIsAlreadyComplete() {
        task.complete();

        assertThrows(TaskAlreadyCompletedException.class, task::complete);
    }

}