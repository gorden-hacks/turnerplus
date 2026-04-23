package de.turnerplus.session.entity;

import de.turnerplus.traininggroup.entity.TrainingGroup;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "training_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "training_group_id", nullable = false)
    private TrainingGroup trainingGroup;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(length = 200)
    private String location;

    @Column(name = "start_time", nullable = false)
    private OffsetDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private OffsetDateTime endTime;

    @Column(name = "registration_deadline")
    private OffsetDateTime registrationDeadline;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @Column(name = "waitlist_enabled", nullable = false)
    private boolean waitlistEnabled;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrainingSessionStatus status;
}