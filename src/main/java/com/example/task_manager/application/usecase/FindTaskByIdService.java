package com.example.task_manager.application.usecase;

import com.example.task_manager.application.dto.TaskOutput;
import com.example.task_manager.application.mapper.TaskApplicationMapper;
import com.example.task_manager.application.port.in.FindTaskByIdUseCase;
import com.example.task_manager.application.port.out.TaskRepositoryPort;
import com.example.task_manager.domain.model.Task;

public class FindTaskByIdService implements FindTaskByIdUseCase {

    private final TaskRepositoryPort taskRepositoryPort;
    private final TaskApplicationMapper taskApplicationMapper;

    public FindTaskByIdService(TaskRepositoryPort taskRepositoryPort,  TaskApplicationMapper taskApplicationMapper) {
        this.taskRepositoryPort = taskRepositoryPort;
        this.taskApplicationMapper = taskApplicationMapper;
    }

    @Override
    public TaskOutput getTaskById(Long id) {
        Task task = taskRepositoryPort.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        return taskApplicationMapper.toTaskOutput(task);
    }
}
