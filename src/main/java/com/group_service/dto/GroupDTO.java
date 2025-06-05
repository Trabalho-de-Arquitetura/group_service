package com.group_service.dto;

import java.util.List;
import java.util.UUID;

public class GroupDTO {
    private final UUID id;
    private final String name;
    private final boolean availableForProjects;
    private final User coordinator;
    private final List<User> students;

    public GroupDTO(UUID id, String name, boolean availableForProjects, User coordinator, List<User> students) {
        this.id = id;
        this.name = name;
        this.availableForProjects = availableForProjects;
        this.coordinator = coordinator;
        this.students = students;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public boolean isAvailableForProjects() { return availableForProjects; }
    public User getCoordinator() { return coordinator; }
    public List<User> getStudents() { return students; }
}