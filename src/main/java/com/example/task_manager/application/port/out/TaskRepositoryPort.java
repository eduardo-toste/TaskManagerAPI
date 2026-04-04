package com.example.task_manager.application.port.out;

import com.example.task_manager.domain.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TaskRepositoryPort {

    Task save(Task task);
    Page<Task> findTasks(Pageable pageable);
    Optional<Task> findById(Long id);

}
