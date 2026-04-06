package com.example.task_manager.application.usecase;

import com.example.task_manager.application.dto.TaskOutput;
import com.example.task_manager.application.exception.TaskNotFoundException;
import com.example.task_manager.application.mapper.TaskApplicationMapper;
import com.example.task_manager.application.port.in.FindTaskUseCase;
import com.example.task_manager.application.port.out.TaskRepositoryPort;
import com.example.task_manager.domain.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class FindTaskService implements FindTaskUseCase {

    private final TaskRepositoryPort taskRepositoryPort;
    private final TaskApplicationMapper taskApplicationMapper;

    public FindTaskService(TaskRepositoryPort taskRepositoryPort, TaskApplicationMapper taskApplicationMapper) {
        this.taskRepositoryPort = taskRepositoryPort;
        this.taskApplicationMapper = taskApplicationMapper;
    }

    @Override
    public Page<TaskOutput> findTasks(Pageable pageable) {
        return taskApplicationMapper.toTaskOutputPage(taskRepositoryPort.findTasks(pageable));
    }

    @Override
    public TaskOutput getTaskById(Long id) {
        Task task = taskRepositoryPort.findById(id)
                .orElseThrow(TaskNotFoundException::new);

        return taskApplicationMapper.toTaskOutput(task);
    }
}
