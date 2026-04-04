package com.example.task_manager.configuration;

import com.example.task_manager.application.mapper.TaskApplicationMapper;
import com.example.task_manager.application.port.in.CreateTaskUseCase;
import com.example.task_manager.application.port.in.FindTaskByIdUseCase;
import com.example.task_manager.application.port.out.TaskRepositoryPort;
import com.example.task_manager.application.usecase.CreateTaskService;
import com.example.task_manager.application.usecase.FindTaskByIdService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaskUseCaseConfig {

    @Bean
    public CreateTaskUseCase createTaskUseCase(TaskRepositoryPort taskRepositoryPort, TaskApplicationMapper taskApplicationMapper) {
        return new CreateTaskService(taskRepositoryPort,  taskApplicationMapper);
    }

    @Bean
    public FindTaskByIdUseCase findTaskByIdUseCase(TaskRepositoryPort taskRepositoryPort, TaskApplicationMapper taskApplicationMapper) {
        return new FindTaskByIdService(taskRepositoryPort,  taskApplicationMapper);
    }

}
