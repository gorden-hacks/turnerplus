package de.turnflow.registration.entity;

import de.turnflow.member.entity.Member;
import de.turnflow.session.entity.TrainingSession;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "registrations",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_registration_session_member", columnNames = {
                        "training_session_id", "member_id"
                })
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "training_session_id", nullable = false)
    private TrainingSession trainingSession;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegistrationStatus status;

    @Column(name = "registered_at", nullable = false)
    private OffsetDateTime registeredAt;

    @Column(name = "cancelled_at")
    private OffsetDateTime cancelledAt;
}