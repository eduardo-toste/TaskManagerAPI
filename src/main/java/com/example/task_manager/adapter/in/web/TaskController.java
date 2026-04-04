package com.example.task_manager.adapter.in.web;

import com.example.task_manager.adapter.in.web.mapper.TaskWebMapper;
import com.example.task_manager.adapter.in.web.request.CreateTaskRequest;
import com.example.task_manager.adapter.in.web.response.TaskResponse;
import com.example.task_manager.application.dto.TaskOutput;
import com.example.task_manager.application.port.in.CreateTaskUseCase;
import com.example.task_manager.application.port.in.FindTaskUseCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final CreateTaskUseCase createTaskUseCase;
    private final FindTaskUseCase findTaskUseCase;
    private final TaskWebMapper taskWebMapper;

    public TaskController(CreateTaskUseCase createTaskUseCase,  TaskWebMapper taskWebMapper,  FindTaskUseCase findTaskUseCase) {
        this.createTaskUseCase = createTaskUseCase;
        this.taskWebMapper = taskWebMapper;
        this.findTaskUseCase = findTaskUseCase;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody CreateTaskRequest request) {
        TaskOutput output = createTaskUseCase.createTask(taskWebMapper.toCommand(request));
        TaskResponse response = taskWebMapper.toResponse(output);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponse>> findTasks(Pageable pageable) {
        Page<TaskOutput> outputPage = findTaskUseCase.findTasks(pageable);
        Page<TaskResponse> response = taskWebMapper.toResponse(outputPage);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        TaskOutput output = findTaskUseCase.getTaskById(id);
        TaskResponse response = taskWebMapper.toResponse(output);
        return ResponseEntity.ok(response);
    }

}
