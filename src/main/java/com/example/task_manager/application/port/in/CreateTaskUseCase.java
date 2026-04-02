package com.example.task_manager.application.port.in;

import com.example.task_manager.application.dto.CreateTaskCommand;
import com.example.task_manager.application.dto.TaskOutput;

public interface CreateTaskUseCase {

    TaskOutput createTask(CreateTaskCommand command);

}
