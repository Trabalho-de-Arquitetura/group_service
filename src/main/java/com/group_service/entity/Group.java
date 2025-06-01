package com.group_service.entity;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private boolean availableForProjects;

    @Column(name = "coordinator_id", nullable = false)
    private UUID coordinatorId; // Armazena o ID do User coordenador

    @ElementCollection(fetch = FetchType.EAGER) // EAGER para simplicidade, considere LAZY
    @CollectionTable(name = "group_students", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "student_id")
    private List<UUID> studentIds; // Armazena IDs dos User estudantes

    public Group() {}

    public Group(String name, boolean availableForProjects, UUID coordinatorId, List<UUID> studentIds) {
        this.name = name;
        this.availableForProjects = availableForProjects;
        this.coordinatorId = coordinatorId;
        this.studentIds = studentIds;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isAvailableForProjects() { return availableForProjects; }
    public void setAvailableForProjects(boolean availableForProjects) { this.availableForProjects = availableForProjects; }
    public UUID getCoordinatorId() { return coordinatorId; }
    public void setCoordinatorId(UUID coordinatorId) { this.coordinatorId = coordinatorId; }
    public List<UUID> getStudentIds() { return studentIds; }
    public void setStudentIds(List<UUID> studentIds) { this.studentIds = studentIds; }
}