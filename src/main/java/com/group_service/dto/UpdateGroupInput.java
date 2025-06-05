package com.group_service.dto;

import java.util.List;
import java.util.UUID;

public class UpdateGroupInput {
    public UUID id;
    public String name;
    public Boolean availableForProjects;
    public UUID coordinatorId;
    public List<UUID> studentIds;

    public UpdateGroupInput() {}
    public UpdateGroupInput(UUID id,String name, boolean availableForProjects, UUID coordinatorId, List<UUID> studentIds) {
        this.id = id;
        this.name = name;
        this.availableForProjects = availableForProjects;
        this.coordinatorId = coordinatorId;
        this.studentIds = studentIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAvailableForProjects() {
        return availableForProjects;
    }

    public void setAvailableForProjects(boolean availableForProjects) {
        this.availableForProjects = availableForProjects;
    }

    public UUID getCoordinatorId() {
        return coordinatorId;
    }

    public void setCoordinatorId(UUID coordinatorId) {
        this.coordinatorId = coordinatorId;
    }

    public List<UUID> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(List<UUID> studentIds) {
        this.studentIds = studentIds;
    }
}
