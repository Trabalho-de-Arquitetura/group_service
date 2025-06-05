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
    private UUID coordinator; // Armazena o ID do User coordenador

    @ElementCollection(fetch = FetchType.EAGER) // EAGER para simplicidade, considere LAZY
    @CollectionTable(name = "group_students", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "student_id")
    private List<UUID> students; // Armazena IDs dos User estudantes

    public Group() {}

    public Group(String name, boolean availableForProjects, UUID coordinator, List<UUID> students) {
        this.name = name;
        this.availableForProjects = availableForProjects;
        this.coordinator = coordinator;
        this.students = students;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isAvailableForProjects() { return availableForProjects; }
    public void setAvailableForProjects(boolean availableForProjects) { this.availableForProjects = availableForProjects; }
    public UUID getCoordinator() { return coordinator; }
    public void setCoordinator(UUID coordinatorId) { this.coordinator = coordinatorId; }
    public List<UUID> getStudents() { return students; }
    public void setStudents(List<UUID> studentIds) { this.students = studentIds; }
}