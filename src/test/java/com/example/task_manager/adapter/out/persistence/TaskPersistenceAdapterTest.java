package com.example.task_manager.adapter.out.persistence;

import com.example.task_manager.adapter.out.persistence.mapper.TaskPersistenceMapper;
import com.example.task_manager.domain.enums.TaskStatus;
import com.example.task_manager.domain.model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({TaskPersistenceAdapter.class, TaskPersistenceMapper.class})
class TaskPersistenceAdapterTest {

    @Autowired
    private TaskPersistenceAdapter taskPersistenceAdapter;

    @Test
    @DisplayName("Should save task successfully")
    void shouldSaveTaskSuccessfully() {
        Task task = new Task(null, "Study", "Hexagonal architecture");

        Task savedTask = taskPersistenceAdapter.save(task);

        assertNotNull(savedTask.getId());
        assertEquals("Study", savedTask.getTitle());
        assertEquals("Hexagonal architecture", savedTask.getDescription());
        assertEquals(TaskStatus.PENDING, savedTask.getStatus());
    }

    @Test
    @DisplayName("Should find task by id successfully")
    void shouldFindTaskByIdSuccessfully() {
        Task task = new Task(null, "Study", "Hexagonal architecture");
        Task savedTask = taskPersistenceAdapter.save(task);

        Optional<Task> result = taskPersistenceAdapter.findById(savedTask.getId());

        assertTrue(result.isPresent());
        assertEquals(savedTask.getId(), result.get().getId());
        assertEquals("Study", result.get().getTitle());
        assertEquals("Hexagonal architecture", result.get().getDescription());
        assertEquals(TaskStatus.PENDING, result.get().getStatus());
    }

    @Test
    @DisplayName("Should return empty when task is not found")
    void shouldReturnEmptyWhenTaskIsNotFound() {
        Optional<Task> result = taskPersistenceAdapter.findById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return paged tasks successfully")
    void shouldReturnPagedTasksSuccessfully() {
        taskPersistenceAdapter.save(new Task(null, "Task 1", "Description 1"));
        taskPersistenceAdapter.save(new Task(null, "Task 2", "Description 2"));
        taskPersistenceAdapter.save(new Task(null, "Task 3", "Description 3"));

        Page<Task> page = taskPersistenceAdapter.findTasks(PageRequest.of(0, 2));

        assertEquals(2, page.getContent().size());
        assertEquals(3, page.getTotalElements());
        assertEquals(2, page.getSize());
        assertEquals(0, page.getNumber());

        assertNotNull(page.getContent().get(0).getId());
        assertNotNull(page.getContent().get(1).getId());
    }

    @Test
    @DisplayName("Should return empty page when there are no tasks")
    void shouldReturnEmptyPageWhenThereAreNoTasks() {
        Page<Task> page = taskPersistenceAdapter.findTasks(PageRequest.of(0, 10));

        assertTrue(page.getContent().isEmpty());
        assertEquals(0, page.getTotalElements());
    }
}