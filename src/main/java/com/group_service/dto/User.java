package com.group_service.dto;

import java.util.UUID;

public class User {
    private UUID id;
    public User(UUID id) { this.id = id; }
    public UUID getId() { return id; }
}