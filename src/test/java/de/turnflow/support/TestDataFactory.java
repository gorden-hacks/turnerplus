package de.turnflow.support;

import de.turnflow.member.*;
import de.turnflow.member.entity.Member;
import de.turnflow.registration.RegistrationRepository;
import de.turnflow.session.TrainingSessionRepository;
import de.turnflow.traininggroup.*;
import de.turnflow.traininggroup.entity.TrainingGroup;
import de.turnflow.user.*;
import de.turnflow.user.entity.Role;
import de.turnflow.user.entity.RoleName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
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
}