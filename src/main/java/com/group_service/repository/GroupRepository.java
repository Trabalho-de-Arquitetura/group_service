package com.group_service.repository;

import com.group_service.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {
    List<Group> findAllByNameIn(List<String> names);
    Group findByName(String name);

    @Query(
            value = "SELECT * FROM groups g WHERE g.coordinator_id = :coordinatorId",
            nativeQuery = true
    )
    List<Group> findAllByCoordinatorId(@Param("coordinatorId") UUID coordinatorId);

    @Query(
            value = "SELECT * FROM groups g JOIN group_students gs ON g.id = gs.group_id WHERE gs.student_id = :studentId",
            nativeQuery = true
    )
    List<Group> findAllByStudentId(@Param("studentId") UUID studentId);
}
