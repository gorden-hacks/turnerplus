package de.turnflow.support;

import de.turnflow.member.*;
import de.turnflow.member.entity.Member;
import de.turnflow.registration.RegistrationRepository;
import de.turnflow.registration.entity.Registration;
import de.turnflow.registration.entity.RegistrationStatus;
import de.turnflow.session.TrainingSessionRepository;
import de.turnflow.session.entity.TrainingSession;
import de.turnflow.session.entity.TrainingSessionStatus;
import de.turnflow.traininggroup.*;
import de.turnflow.traininggroup.entity.MemberGroupPermission;
import de.turnflow.traininggroup.entity.TrainingGroup;
import de.turnflow.user.*;
import de.turnflow.user.entity.Role;
import de.turnflow.user.entity.RoleName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class TestDataFactory {

    private final RegistrationRepository registrationRepository;
    private final TrainingSessionRepository trainingSessionRepository;
    private final MemberGroupPermissionRepository permissionRepository;
    private final TrainerGroupAssignmentRepository trainerAssignmentRepository;
    private final TrainingGroupRepository trainingGroupRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public void cleanDatabase() {
        registrationRepository.deleteAll();
        trainingSessionRepository.deleteAll();
        permissionRepository.deleteAll();
        trainerAssignmentRepository.deleteAll();
        trainingGroupRepository.deleteAll();
        userRepository.deleteAll();
        memberRepository.deleteAll();
    }

    public void ensureRoles() {
        Arrays.stream(RoleName.values()).forEach(roleName ->
                roleRepository.findByName(roleName)
                        .orElseGet(() -> roleRepository.save(
                                Role.builder().name(roleName).build()
                        ))
        );
    }

    public Member member(String firstName, String lastName) {
        return memberRepository.save(Member.builder()
                .firstName(firstName)
                .lastName(lastName)
                .birthDate(LocalDate.of(2012, 1, 1))
                .active(true)
                .build());
    }

    public TrainingGroup group(String name) {
        return trainingGroupRepository.save(TrainingGroup.builder()
                .name(name)
                .description("Testgruppe")
                .active(true)
                .build());
    }

    public TrainingSessionRepository trainingSessionRepository() {
        return trainingSessionRepository;
    }

    public RegistrationRepository registrationRepository() {
        return registrationRepository;
    }

    public TrainingSession session(
            TrainingGroup group,
            String title,
            String startTime,
            TrainingSessionStatus status
    ) {
        OffsetDateTime start = OffsetDateTime.parse(startTime);

        return trainingSessionRepository.save(TrainingSession.builder()
                .trainingGroup(group)
                .title(title)
                .description("Test")
                .location("Turnhalle Nord")
                .startTime(start)
                .endTime(start.plusHours(2))
                .registrationDeadline(start.minusHours(5))
                .maxParticipants(20)
                .waitlistEnabled(true)
                .status(status)
                .build());
    }

    public Registration registration(
            TrainingSession session,
            Member member,
            RegistrationStatus status
    ) {
        return registrationRepository.save(Registration.builder()
                .trainingSession(session)
                .member(member)
                .status(status)
                .registeredAt(OffsetDateTime.parse("2026-05-01T12:00:00+02:00"))
                .build());
    }

    public MemberGroupPermission memberGroupPermission(Member member, TrainingGroup group) {
        return permissionRepository.save(MemberGroupPermission.builder()
                .member(member)
                .trainingGroup(group)
                .validFrom(LocalDate.of(2026, 1, 1))
                .validTo(null)
                .active(true)
                .build());
    }
}