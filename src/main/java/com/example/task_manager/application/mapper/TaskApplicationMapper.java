package com.example.task_manager.application.mapper;

import com.example.task_manager.application.dto.TaskOutput;
import com.example.task_manager.domain.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskApplicationMapper {

    public TaskOutput toTaskOutput(Task task) {
        return new TaskOutput(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus()
        );
    }

}
