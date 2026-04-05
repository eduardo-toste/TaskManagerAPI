package com.example.task_manager.application.dto;

public record CreateTaskCommand(

        String title,
        String description

) {
}
