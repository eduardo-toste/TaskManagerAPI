package com.example.task_manager.application.usecase;

import com.example.task_manager.application.dto.TaskOutput;
import com.example.task_manager.application.mapper.TaskApplicationMapper;
import com.example.task_manager.application.port.in.CompleteTaskUseCase;
import com.example.task_manager.application.port.out.TaskRepositoryPort;
import com.example.task_manager.domain.model.Task;

public class CompleteTaskService implements CompleteTaskUseCase {

    private final TaskRepositoryPort taskRepositoryPort;
    private final TaskApplicationMapper taskApplicationMapper;

    public CompleteTaskService(TaskRepositoryPort taskRepositoryPort,  TaskApplicationMapper taskApplicationMapper) {
        this.taskRepositoryPort = taskRepositoryPort;
        this.taskApplicationMapper = taskApplicationMapper;
    }

    @Override
    public TaskOutput completeTask(Long taskId) {
        Task task = taskRepositoryPort.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.complete();
        Task savedTask = taskRepositoryPort.save(task);

        return taskApplicationMapper.toTaskOutput(savedTask);
    }

}
