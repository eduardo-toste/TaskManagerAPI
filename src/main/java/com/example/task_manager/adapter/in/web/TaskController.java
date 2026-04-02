package com.example.task_manager.adapter.in.web;

import com.example.task_manager.adapter.in.web.mapper.TaskWebMapper;
import com.example.task_manager.adapter.in.web.request.CreateTaskRequest;
import com.example.task_manager.adapter.in.web.response.TaskResponse;
import com.example.task_manager.application.dto.TaskOutput;
import com.example.task_manager.application.port.in.CreateTaskUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final CreateTaskUseCase createTaskUseCase;
    private final TaskWebMapper taskWebMapper;

    public TaskController(CreateTaskUseCase createTaskUseCase,  TaskWebMapper taskWebMapper) {
        this.createTaskUseCase = createTaskUseCase;
        this.taskWebMapper = taskWebMapper;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody CreateTaskRequest request) {
        TaskOutput output = createTaskUseCase.createTask(taskWebMapper.toCommand(request));
        TaskResponse response = taskWebMapper.toResponse(output);
        return ResponseEntity.ok(response);
    }

}
