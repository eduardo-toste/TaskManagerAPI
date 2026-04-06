package com.example.task_manager.domain.exception;

public class TaskAlreadyCompletedException extends RuntimeException {

    public TaskAlreadyCompletedException() {
        super("Task is already completed.");
    }

}
