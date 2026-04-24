package de.turnflow.traininggroup;

import de.turnflow.traininggroup.entity.TrainingGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingGroupRepository extends JpaRepository<TrainingGroup, Long> {

    boolean existsByName(String name);
}