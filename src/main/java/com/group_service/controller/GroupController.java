package com.group_service.controller;

import com.group_service.dto.CreateGroupInput;
import com.group_service.dto.GroupDTO;
import com.group_service.dto.UpdateGroupInput;
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
    public Group findAllGroupsById(@Argument List<UUID> id) {
        return groupRepository.findAllById(id).stream().findFirst().orElse(null);
    }
    @QueryMapping
    public List<GroupDTO> findAllGroups() {
        return groupRepository.findAll().stream()
                .map(group -> new GroupDTO(
                        group.getId(),
                        group.getName(),
                        group.isAvailableForProjects(),
                        new User(group.getCoordinatorId()),
                        group.getStudentIds().stream().map(User::new).toList()
                ))
                .toList();
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
    public GroupDTO saveGroup(@Argument CreateGroupInput input) {
        Group group = new Group();
        group.setName(input.name);
        group.setAvailableForProjects(input.availableForProjects);
        group.setCoordinatorId(input.coordinatorId);
        group.setStudentIds(input.studentIds);

        Group savedGroup = groupRepository.save(group);
        System.out.println("Group saved: " + savedGroup.getId() + ", Name: " + savedGroup.getName());
        return new GroupDTO(
                savedGroup.getId(),
                savedGroup.getName(),
                savedGroup.isAvailableForProjects(),
                new User(savedGroup.getCoordinatorId()),
                savedGroup.getStudentIds().stream().map(User::new).toList()
        );
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
            group.setCoordinatorId(input.coordinatorId);
        if (input.studentIds != null)
            group.setStudentIds(input.studentIds);
        else
            group.setStudentIds(List.of());

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
    public User coordinator(GroupDTO groupDTO) {
        if (groupDTO.getCoordinator() == null) {
            throw new IllegalStateException("Coordinator ID is null for group: " + groupDTO.getId());
        }
        return new User(groupDTO.getCoordinator().getId());
    }

    // Resolver para o campo 'students' do tipo 'Group'.
    // Retorna uma lista de User (stubs) contendo apenas os IDs.
    @SchemaMapping(typeName = "GroupDTO", field = "students")
    public List<User> students(GroupDTO groupDTO) {
        if (groupDTO.getStudents() == null || groupDTO.getStudents().isEmpty()) {
            return List.of();
        }
        return groupDTO.getStudents().stream()
                .map(student -> new User(student.getId()))
                .collect(Collectors.toList());
    }

    @SchemaMapping(typeName = "GroupDTO", field = "id")
    public Optional<Group> __resolveReference(Group group) {
        return groupRepository.findById(group.getId());
    }
}