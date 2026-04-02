package com.example.task_manager.adapter.out.persistence.repository;

import com.example.task_manager.adapter.out.persistence.entity.TaskJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskJpaRepository extends JpaRepository<TaskJpaEntity, Long> {
}
