package com.example.taskmanager.controller;

import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;


    @GetMapping
    public List<TaskResponse> getAll() {
        return service.findAll().stream().map(TaskResponse::from).toList();
    }

    @GetMapping("/{id}")
    public TaskResponse getOne(@PathVariable Long id) {
        return TaskResponse.from(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskRequest req) {
        return ResponseEntity.status(201).body(TaskResponse.from(service.create(req)));
    }

    @PutMapping("/{id}")
    public TaskResponse update(@PathVariable Long id, @Valid @RequestBody TaskRequest req) {
        return TaskResponse.from(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}