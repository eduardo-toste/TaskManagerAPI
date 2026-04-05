package com.example.task_manager.application.port.in;

import com.example.task_manager.application.dto.TaskOutput;

public interface CompleteTaskUseCase {

    TaskOutput completeTask(Long taskId);

}
