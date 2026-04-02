package com.example.task_manager.adapter.out.persistence;

import com.example.task_manager.adapter.out.persistence.entity.TaskJpaEntity;
import com.example.task_manager.adapter.out.persistence.mapper.TaskPersistenceMapper;
import com.example.task_manager.adapter.out.persistence.repository.TaskJpaRepository;
import com.example.task_manager.application.port.out.TaskRepositoryPort;
import com.example.task_manager.domain.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskPersistenceAdapter implements TaskRepositoryPort {

    private final TaskJpaRepository taskJpaRepository;
    private final TaskPersistenceMapper taskPersistenceMapper;

    public TaskPersistenceAdapter(TaskJpaRepository taskJpaRepository, TaskPersistenceMapper taskPersistenceMapper) {
        this.taskJpaRepository = taskJpaRepository;
        this.taskPersistenceMapper = taskPersistenceMapper;
    }

    @Override
    public Task save(Task task) {
        TaskJpaEntity entity = taskPersistenceMapper.toJpaEntity(task);
        TaskJpaEntity savedEntity = taskJpaRepository.save(entity);
        return taskPersistenceMapper.toDomainEntity(savedEntity);
    }
}
