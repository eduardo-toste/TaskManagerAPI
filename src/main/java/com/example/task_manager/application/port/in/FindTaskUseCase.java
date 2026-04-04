package com.example.task_manager.application.port.in;

import com.example.task_manager.application.dto.TaskOutput;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindTaskUseCase {

    Page<TaskOutput> findTasks(Pageable pageable);
    TaskOutput getTaskById(Long id);

}
