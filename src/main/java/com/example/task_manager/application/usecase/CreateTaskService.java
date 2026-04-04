package com.example.task_manager.application.usecase;

import com.example.task_manager.application.dto.CreateTaskCommand;
import com.example.task_manager.application.dto.TaskOutput;
import com.example.task_manager.application.mapper.TaskApplicationMapper;
import com.example.task_manager.application.port.in.CreateTaskUseCase;
import com.example.task_manager.application.port.out.TaskRepositoryPort;
import com.example.task_manager.domain.model.Task;

public class CreateTaskService implements CreateTaskUseCase {

    private final TaskRepositoryPort taskRepositoryPort;
    private final TaskApplicationMapper taskApplicationMapper;

    public CreateTaskService(TaskRepositoryPort taskRepositoryPort,  TaskApplicationMapper taskApplicationMapper) {
        this.taskRepositoryPort = taskRepositoryPort;
        this.taskApplicationMapper = taskApplicationMapper;
    }

    @Override
    public TaskOutput createTask(CreateTaskCommand command) {
        Task task = new Task(
                null,
                command.title(),
                command.description()
        );

        Task savedTask = taskRepositoryPort.save(task);

        return taskApplicationMapper.toTaskOutput(task);
    }
}
