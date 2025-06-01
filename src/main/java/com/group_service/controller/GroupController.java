package com.group_service.controller;

import com.group_service.dto.CreateGroupInput;
import com.group_service.dto.User;
import com.group_service.entity.Group;
import com.group_service.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public List<Group> allGroups() {
        return groupRepository.findAll();
    }

    @MutationMapping
    public Group createGroup(@Argument CreateGroupInput input) {
        Group group = new Group();
        group.setName(input.name);
        group.setAvailableForProjects(input.availableForProjects);
        group.setCoordinatorId(input.coordinatorId);
        group.setStudentIds(input.studentIds);
        return groupRepository.save(group);
    }

    // Resolver para o campo 'coordinator' do tipo 'Group'.
    // Retorna um objeto User (stub) contendo apenas o ID.
    // O Gateway usará esse ID para buscar o User completo do UserService.
    @SchemaMapping(typeName = "Group", field = "coordinator")
    public User coordinator(Group group) {
        return new User(group.getCoordinatorId());
    }

    // Resolver para o campo 'students' do tipo 'Group'.
    // Retorna uma lista de User (stubs) contendo apenas os IDs.
    @SchemaMapping(typeName = "Group", field = "students")
    public List<User> students(Group group) {
        if (group.getStudentIds() == null) {
            return List.of();
        }
        return group.getStudentIds().stream()
                .map(User::new) // Cria um User stub para cada ID
                .collect(Collectors.toList());
    }

    @SchemaMapping(typeName = "Group", field = "id")
    public Optional<Group> __resolveReference(Group group) {
        return groupRepository.findById(group.getId());
    }
}