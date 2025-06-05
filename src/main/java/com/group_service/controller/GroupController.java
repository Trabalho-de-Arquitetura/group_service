package com.group_service.controller;

import com.group_service.dto.CreateGroupInput;
import com.group_service.dto.UpdateGroupInput;
import com.group_service.dto.User;
import com.group_service.entity.Group;
import com.group_service.repository.GroupRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class GroupController {

    private final GroupRepository groupRepository;

    public GroupController(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @QueryMapping
    public Group groupById(@Argument UUID id) {
        return groupRepository.findById(id).orElse(null);
    }
    @QueryMapping
    public List<Group> findAllGroupsById(@Argument List<UUID> id) {
        return groupRepository.findAllById(id);
    }
    @QueryMapping
    public List<Group> findAllGroups() {
        return groupRepository.findAll();
    }

    @QueryMapping
    public List<Group> findAllGroupsByNameIn(@Argument List<String> names) {
        return groupRepository.findAllByNameIn(names);
    }

    @QueryMapping
    public List<Group> findAllGroupByCoordinator(@Argument UUID coordinator_id) {
        return groupRepository.findAllByCoordinatorId(coordinator_id);
    }

    @QueryMapping
    public List<Group> findAllGroupsByStudentId(@Argument UUID student_id) {
        return groupRepository.findAllByStudentId(student_id);
    }

    @MutationMapping
    public Group saveGroup(@Argument CreateGroupInput input) {
        Group group = new Group();
        group.setName(input.name);
        group.setAvailableForProjects(input.availableForProjects);
        group.setCoordinator(input.coordinatorId);
        group.setStudents(input.studentIds);

        return groupRepository.save(group);
    }
    @MutationMapping
    public Group updateGroup(@Argument UpdateGroupInput input) {
        Group group = groupRepository.findById(input.id)
                .orElseThrow(() -> new IllegalArgumentException("Group not found with id: " + input.id));
        if (input.name != null)
            group.setName(input.name);
        if (input.availableForProjects != null)
            group.setAvailableForProjects(input.availableForProjects);
        if (input.coordinatorId != null)
            group.setCoordinator(input.coordinatorId);
        if (input.studentIds != null)
            group.setStudents(input.studentIds);
        else
            group.setStudents(List.of());

        return groupRepository.save(group);
    }
    @MutationMapping
    public Group deleteGroup(@Argument UUID id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Group not found with id: " + id));
        groupRepository.delete(group);
        return group;
    }

    // Resolver para o campo 'coordinator' do tipo 'Group'.
    // Retorna um objeto User (stub) contendo apenas o ID.
    // O Gateway usará esse ID para buscar o User completo do UserService.
    @SchemaMapping(typeName = "GroupDTO", field = "coordinator")
    public User coordinator(Group group) {
        if (group.getCoordinator() == null) {
            throw new IllegalStateException("Coordinator ID is null for group: " + group.getId());
        }
        return new User(group.getCoordinator());
    }

    @SchemaMapping(typeName = "GroupDTO", field = "students")
    public List<User> students(Group group) {
        return Optional.ofNullable(group.getStudents())
                .map(this::convertToUsers)
                .orElse(List.of());
    }

    private List<User> convertToUsers(List<UUID> studentIds) {
        return studentIds.stream()
                .map(User::new)
                .toList();
    }

    @SchemaMapping(typeName = "GroupDTO", field= "id")
    public UUID __resolveReference(Group group) {
        return groupRepository.findById(group.getId()).get().getId();
    }
}