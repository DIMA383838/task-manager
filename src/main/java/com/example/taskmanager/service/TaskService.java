
package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.repository.TaskRepository;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private static final Logger log =
            LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository repository;
    private final MeterRegistry meterRegistry;

    // Просмотр списка задач
    public List<Task> findAll() {
        List<Task> tasks = repository.findAll();

        meterRegistry.counter("tasks_list_requests_total").increment();

        log.info("Retrieved task list: count={}", tasks.size());

        return tasks;
    }

    // Просмотр одной задачи
    public Task findById(Long id) {
        return repository.findById(id)
                .map(task -> {
                    log.info("Task retrieved: id={}", id);
                    return task;
                })
                .orElseThrow(() -> {
                    log.warn("Task not found: id={}", id);
                    return new TaskNotFoundException(id);
                });
    }

    // Создание задачи
    @Transactional
    public Task create(TaskRequest req) {
        log.info("Creating new task");

        Task task = new Task();
        task.setTitle(req.getTitle());
        task.setDescription(req.getDescription());
        task.setStatus(
                req.getStatus() != null ? req.getStatus() : "NEW"
        );

        Task savedTask = repository.save(task);

        meterRegistry.counter("tasks_created").increment();

        log.info(
                "Task created successfully: id={}, status={}",
                savedTask.getId(),
                savedTask.getStatus()
        );

        return savedTask;
    }

    // Изменение задачи
    @Transactional
    public Task update(Long id, TaskRequest req) {
        log.info("Updating task: id={}", id);

        Task task = findById(id);

        task.setTitle(req.getTitle());
        task.setDescription(req.getDescription());

        if (req.getStatus() != null) {
            task.setStatus(req.getStatus());
        }

        Task savedTask = repository.save(task);

        meterRegistry.counter("tasks_updated_total").increment();

        log.info(
                "Task updated successfully: id={}, status={}",
                savedTask.getId(),
                savedTask.getStatus()
        );

        return savedTask;
    }

    // Удаление задачи
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            log.warn("Cannot delete task: not found, id={}", id);
            throw new TaskNotFoundException(id);
        }

        repository.deleteById(id);

        meterRegistry.counter("tasks_deleted_total").increment();

        log.info("Task deleted successfully: id={}", id);
    }
}
