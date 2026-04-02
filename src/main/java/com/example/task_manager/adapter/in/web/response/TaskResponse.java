package com.example.task_manager.adapter.in.web.response;

import com.example.task_manager.domain.enums.TaskStatus;

public record TaskResponse(

        Long id,
        String title,
        String description,
        TaskStatus status

) {
}
