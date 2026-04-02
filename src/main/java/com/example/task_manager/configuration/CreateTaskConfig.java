package com.example.task_manager.configuration;

import com.example.task_manager.application.port.in.CreateTaskUseCase;
import com.example.task_manager.application.port.out.TaskRepositoryPort;
import com.example.task_manager.application.usecase.CreateTaskService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CreateTaskConfig {

    @Bean
    public CreateTaskUseCase createTaskUseCase(TaskRepositoryPort taskRepositoryPort) {
        return new CreateTaskService(taskRepositoryPort);
    }

}
