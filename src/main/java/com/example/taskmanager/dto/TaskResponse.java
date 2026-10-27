package com.example.taskmanager.dto;

import com.example.taskmanager.entity.Task;
import lombok.Getter;

import java.time.Instant;

@Getter
public class TaskResponse {
    private final Long id;
    private final String title;
    private final String description;
    private final String status;
    private final Instant createdAt;
    private final Instant updatedAt;

    private TaskResponse(Task t) {
        this.id = t.getId();
        this.title = t.getTitle();
        this.description = t.getDescription();
        this.status = t.getStatus();
        this.createdAt = t.getCreatedAt();
        this.updatedAt = t.getUpdatedAt();
    }

    public static TaskResponse from(Task t) {
        return new TaskResponse(t);
    }
}