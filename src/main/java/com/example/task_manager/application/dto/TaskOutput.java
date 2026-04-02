package com.example.task_manager.application.dto;

import com.example.task_manager.domain.enums.TaskStatus;

public record TaskOutput(

        Long id,
        String title,
        String description,
        TaskStatus status

) {
}
