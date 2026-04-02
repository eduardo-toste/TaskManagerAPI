package com.example.task_manager.adapter.in.web.mapper;

import com.example.task_manager.adapter.in.web.request.CreateTaskRequest;
import com.example.task_manager.adapter.in.web.response.TaskResponse;
import com.example.task_manager.application.dto.CreateTaskCommand;
import com.example.task_manager.application.dto.TaskOutput;
import org.springframework.stereotype.Component;

@Component
public class TaskWebMapper {

    public CreateTaskCommand toCommand(CreateTaskRequest request) {
        return new CreateTaskCommand(
                request.title(),
                request.description()
        );
    }

    public TaskResponse toResponse(TaskOutput taskOutput) {
        return new TaskResponse(
                taskOutput.id(),
                taskOutput.title(),
                taskOutput.description(),
                taskOutput.status()
        );
    }

}
