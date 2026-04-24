package de.turnflow.traininggroup;

import de.turnflow.traininggroup.entity.TrainerGroupAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainerGroupAssignmentRepository
        extends JpaRepository<TrainerGroupAssignment, Long> {

    boolean existsByUserIdAndTrainingGroupId(Long userId, Long trainingGroupId);

    List<TrainerGroupAssignment> findByUserId(Long userId);

    List<TrainerGroupAssignment> findByTrainingGroupId(Long trainingGroupId);
}