package com.example.task_manager.application.port.out;

import com.example.task_manager.domain.model.Task;

import java.util.Optional;

public interface TaskRepositoryPort {

    Task save(Task task);
    Optional<Task> findById(Long id);

}
