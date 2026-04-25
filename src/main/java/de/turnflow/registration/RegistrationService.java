package de.turnflow.registration;

import de.turnflow.common.exception.BusinessException;
import de.turnflow.common.exception.ErrorCode;
import de.turnflow.common.exception.NotFoundException;

import de.turnflow.member.MemberRepository;
import de.turnflow.member.entity.Member;
import de.turnflow.registration.dto.RegistrationDto;
import de.turnflow.registration.entity.Registration;
import de.turnflow.registration.entity.RegistrationStatus;
import de.turnflow.registration.mapper.RegistrationMapper;
import de.turnflow.session.TrainingSessionRepository;
import de.turnflow.session.entity.TrainingSession;
import de.turnflow.session.entity.TrainingSessionStatus;
import de.turnflow.traininggroup.MemberGroupPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final TrainingSessionRepository trainingSessionRepository;
    private final MemberRepository memberRepository;
    private final MemberGroupPermissionRepository permissionRepository;
    private final RegistrationMapper registrationMapper;

    @Transactional
    public RegistrationDto register(Long sessionId, Long memberId) {
        TrainingSession session = trainingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TRAINING_SESSION_NOT_FOUND, sessionId));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND, memberId));

        validateRegistrationAllowed(session, member);

        Registration registration = registrationRepository
                .findByTrainingSessionIdAndMemberId(sessionId, memberId)
                .orElse(null);

        if (registration != null && registration.getStatus() == RegistrationStatus.REGISTERED) {
            throw new BusinessException(ErrorCode.MEMBER_ALREADY_REGISTERED);
        }

        RegistrationStatus targetStatus = determineRegistrationStatus(session);

        if (registration == null) {
            registration = Registration.builder()
                    .trainingSession(session)
                    .member(member)
                    .status(targetStatus)
                    .registeredAt(OffsetDateTime.now())
                    .build();
        } else {
            registration.setStatus(targetStatus);
            registration.setRegisteredAt(OffsetDateTime.now());
            registration.setCancelledAt(null);
        }

        Registration saved = registrationRepository.save(registration);
        return registrationMapper.toDto(saved);
    }

    @Transactional
    public RegistrationDto unregister(Long sessionId, Long memberId) {
        TrainingSession session = trainingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TRAINING_SESSION_NOT_FOUND, sessionId));

        Registration registration = registrationRepository
                .findByTrainingSessionIdAndMemberId(sessionId, memberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.REGISTRATION_NOT_FOUND,
                        "sessionId=" + sessionId + ", memberId=" + memberId));

        if (session.getEndTime().isBefore(OffsetDateTime.now())) {
            throw new BusinessException(ErrorCode.UNREGISTER_AFTER_SESSION_END_NOT_ALLOWED);
        }

        if (registration.getStatus() == RegistrationStatus.CANCELLED) {
            throw new BusinessException(ErrorCode.MEMBER_ALREADY_REGISTERED);
        }

        registration.setStatus(RegistrationStatus.CANCELLED);
        registration.setCancelledAt(OffsetDateTime.now());

        Registration saved = registrationRepository.save(registration);
        return registrationMapper.toDto(saved);
    }

    private void validateRegistrationAllowed(TrainingSession session, Member member) {
        if (!member.isActive()) {
            throw new BusinessException(ErrorCode.MEMBER_INACTIVE);
        }

        if (session.getStatus() != TrainingSessionStatus.OPEN) {
            throw new BusinessException(ErrorCode.TRAINING_SESSION_NOT_OPEN);
        }

        OffsetDateTime now = OffsetDateTime.now();

        if (session.getEndTime().isBefore(now)) {
            throw new BusinessException(ErrorCode.TRAINING_SESSION_IN_PAST);
        }

        if (session.getRegistrationDeadline() != null
                && session.getRegistrationDeadline().isBefore(now)) {
            throw new BusinessException(ErrorCode.REGISTRATION_DEADLINE_EXPIRED);
        }

        Long groupId = session.getTrainingGroup().getId();

        boolean hasPermission = permissionRepository.hasValidPermission(
                member.getId(),
                groupId,
                LocalDate.now()
        );

        if (!hasPermission) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_ALLOWED_FOR_GROUP);
        }
    }

    private RegistrationStatus determineRegistrationStatus(TrainingSession session) {
        if (session.getMaxParticipants() == null) {
            return RegistrationStatus.REGISTERED;
        }

        long registeredCount = registrationRepository.countByTrainingSessionIdAndStatus(
                session.getId(),
                RegistrationStatus.REGISTERED
        );

        if (registeredCount < session.getMaxParticipants()) {
            return RegistrationStatus.REGISTERED;
        }

        if (session.isWaitlistEnabled()) {
            return RegistrationStatus.WAITLIST;
        }

        throw new BusinessException(ErrorCode.TRAINING_SESSION_FULL);
    }
}