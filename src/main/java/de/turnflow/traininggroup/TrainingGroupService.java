package de.turnflow.traininggroup;

import de.turnflow.common.exception.BusinessException;
import de.turnflow.common.exception.NotFoundException;

import de.turnflow.member.MemberRepository;
import de.turnflow.member.entity.Member;
import de.turnflow.traininggroup.dto.*;
import de.turnflow.traininggroup.entity.MemberGroupPermission;
import de.turnflow.traininggroup.entity.TrainerGroupAssignment;
import de.turnflow.traininggroup.entity.TrainingGroup;
import de.turnflow.traininggroup.mapper.MemberGroupPermissionMapper;
import de.turnflow.traininggroup.mapper.TrainerGroupAssignmentMapper;
import de.turnflow.traininggroup.mapper.TrainingGroupMapper;
import de.turnflow.user.UserRepository;
import de.turnflow.user.entity.RoleName;
import de.turnflow.user.entity.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TrainingGroupService {

    private final TrainingGroupRepository trainingGroupRepository;
    private final MemberGroupPermissionRepository permissionRepository;
    private final TrainerGroupAssignmentRepository trainerAssignmentRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;

    private final TrainingGroupMapper trainingGroupMapper;
    private final MemberGroupPermissionMapper permissionMapper;
    private final TrainerGroupAssignmentMapper trainerAssignmentMapper;

    @Transactional(readOnly = true)
    public List<TrainingGroupDto> findAll() {
        return trainingGroupMapper.toDtoList(trainingGroupRepository.findAll());
    }

    @Transactional(readOnly = true)
    public TrainingGroupDto findById(Long id) {
        return trainingGroupMapper.toDto(getGroup(id));
    }

    public TrainingGroupDto create(CreateTrainingGroupRequest request) {
        if (trainingGroupRepository.existsByName(request.getName())) {
            throw new BusinessException("Trainingsgruppe existiert bereits: " + request.getName());
        }

        TrainingGroup group = trainingGroupMapper.toEntity(request);
        return trainingGroupMapper.toDto(trainingGroupRepository.save(group));
    }

    public TrainingGroupDto update(Long id, UpdateTrainingGroupRequest request) {
        TrainingGroup group = getGroup(id);

        if (request.getName() != null
                && !request.getName().equals(group.getName())
                && trainingGroupRepository.existsByName(request.getName())) {
            throw new BusinessException("Trainingsgruppe existiert bereits: " + request.getName());
        }

        trainingGroupMapper.update(request, group);

        if (request.getActive() != null) {
            group.setActive(request.getActive());
        }

        return trainingGroupMapper.toDto(trainingGroupRepository.save(group));
    }

    public MemberGroupPermissionDto addMemberPermission(CreateMemberGroupPermissionRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new NotFoundException("Mitglied nicht gefunden: " + request.getMemberId()));

        TrainingGroup group = getGroup(request.getTrainingGroupId());

        if (permissionRepository.existsByMemberIdAndTrainingGroupId(member.getId(), group.getId())) {
            throw new BusinessException("Mitglied ist dieser Trainingsgruppe bereits zugeordnet");
        }

        if (request.getValidTo() != null && request.getValidTo().isBefore(request.getValidFrom())) {
            throw new BusinessException("validTo darf nicht vor validFrom liegen");
        }

        MemberGroupPermission permission = MemberGroupPermission.builder()
                .member(member)
                .trainingGroup(group)
                .validFrom(request.getValidFrom())
                .validTo(request.getValidTo())
                .active(true)
                .build();

        return permissionMapper.toDto(permissionRepository.save(permission));
    }

    public void removeMemberPermission(Long permissionId) {
        MemberGroupPermission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new NotFoundException("Gruppenberechtigung nicht gefunden: " + permissionId));

        permissionRepository.delete(permission);
    }

    @Transactional(readOnly = true)
    public List<MemberGroupPermissionDto> findPermissionsByGroup(Long groupId) {
        return permissionRepository.findByTrainingGroupId(groupId)
                .stream()
                .map(permissionMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MemberGroupPermissionDto> findPermissionsByMember(Long memberId) {
        return permissionRepository.findByMemberId(memberId)
                .stream()
                .map(permissionMapper::toDto)
                .toList();
    }

    public TrainerGroupAssignmentDto addTrainerAssignment(CreateTrainerGroupAssignmentRequest request) {
        UserAccount user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User nicht gefunden: " + request.getUserId()));

        TrainingGroup group = getGroup(request.getTrainingGroupId());

        boolean isTrainer = user.getRoles()
                .stream()
                .anyMatch(role -> role.getName() == RoleName.ROLE_TRAINER);

        if (!isTrainer) {
            throw new BusinessException("User hat nicht die Rolle ROLE_TRAINER");
        }

        if (trainerAssignmentRepository.existsByUserIdAndTrainingGroupId(user.getId(), group.getId())) {
            throw new BusinessException("Trainer ist dieser Trainingsgruppe bereits zugeordnet");
        }

        TrainerGroupAssignment assignment = TrainerGroupAssignment.builder()
                .user(user)
                .trainingGroup(group)
                .build();

        return trainerAssignmentMapper.toDto(trainerAssignmentRepository.save(assignment));
    }

    public void removeTrainerAssignment(Long assignmentId) {
        TrainerGroupAssignment assignment = trainerAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Trainer-Zuordnung nicht gefunden: " + assignmentId));

        trainerAssignmentRepository.delete(assignment);
    }

    @Transactional(readOnly = true)
    public List<TrainerGroupAssignmentDto> findTrainerAssignmentsByGroup(Long groupId) {
        return trainerAssignmentRepository.findByTrainingGroupId(groupId)
                .stream()
                .map(trainerAssignmentMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TrainerGroupAssignmentDto> findTrainerAssignmentsByUser(Long userId) {
        return trainerAssignmentRepository.findByUserId(userId)
                .stream()
                .map(trainerAssignmentMapper::toDto)
                .toList();
    }

    private TrainingGroup getGroup(Long id) {
        return trainingGroupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Trainingsgruppe nicht gefunden: " + id));
    }
}