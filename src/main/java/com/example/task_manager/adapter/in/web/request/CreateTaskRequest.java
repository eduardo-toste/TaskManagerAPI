package com.example.task_manager.adapter.in.web.request;

import jakarta.validation.constraints.NotBlank;

public record CreateTaskRequest(

        @NotBlank String title,
        @NotBlank String description

) {
}
