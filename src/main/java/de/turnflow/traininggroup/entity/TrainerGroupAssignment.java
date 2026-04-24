package de.turnflow.traininggroup.entity;

import de.turnflow.user.entity.UserAccount;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "trainer_group_assignments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_trainer_group_assignment",
                        columnNames = {"user_id", "training_group_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerGroupAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "training_group_id", nullable = false)
    private TrainingGroup trainingGroup;
}