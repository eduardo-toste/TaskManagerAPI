package com.example.task_manager.adapter.out.persistence.mapper;

import com.example.task_manager.adapter.out.persistence.entity.TaskJpaEntity;
import com.example.task_manager.domain.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class TaskPersistenceMapper {

    public TaskJpaEntity toJpaEntity(Task task) {
        return new TaskJpaEntity(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus()
        );
    }

    public Task toDomainEntity(TaskJpaEntity entity) {
        return new Task(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription()
        );
    }

    public Page<Task> toDomainPage(Page<TaskJpaEntity> page) {
        return page.map(this::toDomainEntity);
    }

}
