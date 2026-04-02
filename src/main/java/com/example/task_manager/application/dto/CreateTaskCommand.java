package com.example.task_manager.application.dto;

import com.example.task_manager.domain.enums.TaskStatus;

public record CreateTaskCommand(

        String title,
        String description

) {
}
