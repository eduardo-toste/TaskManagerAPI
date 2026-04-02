package com.example.task_manager.application.port.out;

import com.example.task_manager.domain.model.Task;

public interface TaskRepositoryPort {

    Task save(Task task);

}
