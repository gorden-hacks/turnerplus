package de.turnflow.session;

import de.turnflow.common.exception.ErrorCode;
import de.turnflow.common.exception.NotFoundException;
import de.turnflow.registration.RegistrationRepository;
import de.turnflow.registration.entity.RegistrationStatus;
import de.turnflow.session.dto.*;
import de.turnflow.session.entity.TrainingSession;
import de.turnflow.session.entity.TrainingSessionStatus;
import de.turnflow.session.mapper.TrainingSessionMapper;
import de.turnflow.traininggroup.TrainingGroupRepository;
import de.turnflow.traininggroup.entity.TrainingGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TrainingSessionService {

    private final TrainingSessionRepository trainingSessionRepository;
    private final TrainingGroupRepository trainingGroupRepository;
    private final RegistrationRepository registrationRepository;
    private final TrainingSessionMapper trainingSessionMapper;

    @Transactional(readOnly = true)
    public List<TrainingSessionDto> findFiltered(
            Long groupId,
            TrainingSessionStatus status,
            OffsetDateTime from,
            OffsetDateTime to
    ) {
        return trainingSessionRepository.findFiltered(groupId, status, from, to)
                .stream()
                .map(this::toDtoWithCounts)
                .toList();
    }

    @Transactional(readOnly = true)
    public TrainingSessionDto findById(Long id) {
        TrainingSession session = getSession(id);
        return toDtoWithCounts(session);
    }

    public TrainingSessionDto create(CreateTrainingSessionRequest request) {

        TrainingGroup group = trainingGroupRepository.findById(request.getTrainingGroupId())
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.TRAINING_GROUP_NOT_FOUND,
                        request.getTrainingGroupId()
                ));

        TrainingSession session = TrainingSession.builder()
                .trainingGroup(group)
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .registrationDeadline(request.getRegistrationDeadline())
                .maxParticipants(request.getMaxParticipants())
                .waitlistEnabled(request.isWaitlistEnabled())
                .status(request.getStatus() != null ? request.getStatus() : TrainingSessionStatus.OPEN)
                .build();

        return toDtoWithCounts(trainingSessionRepository.save(session));
    }

    public TrainingSessionDto update(Long id, UpdateTrainingSessionRequest request) {
        TrainingSession session = getSession(id);

        if (request.getTrainingGroupId() != null) {
            TrainingGroup group = trainingGroupRepository.findById(request.getTrainingGroupId())
                    .orElseThrow(() -> new NotFoundException(
                            ErrorCode.TRAINING_GROUP_NOT_FOUND,
                            request.getTrainingGroupId()
                    ));
            session.setTrainingGroup(group);
        }

        trainingSessionMapper.update(request, session);

        if (request.getWaitlistEnabled() != null) {
            session.setWaitlistEnabled(request.getWaitlistEnabled());
        }

        return toDtoWithCounts(trainingSessionRepository.save(session));
    }

    public TrainingSessionDto updateStatus(Long id, UpdateTrainingSessionStatusRequest request) {
        TrainingSession session = getSession(id);
        session.setStatus(request.getStatus());
        return toDtoWithCounts(trainingSessionRepository.save(session));
    }

    public void delete(Long id) {
        TrainingSession session = getSession(id);
        trainingSessionRepository.delete(session);
    }

    @Transactional(readOnly = true)
    public List<TrainingSessionCalendarDto> calendar(
            OffsetDateTime from,
            OffsetDateTime to,
            Long groupId
    ) {
        return trainingSessionRepository.findCalendarSessions(from, to, groupId)
                .stream()
                .map(this::toCalendarDtoWithCounts)
                .toList();
    }

    private TrainingSession getSession(Long id) {
        return trainingSessionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.TRAINING_SESSION_NOT_FOUND,
                        id
                ));
    }

    private TrainingSessionDto toDtoWithCounts(TrainingSession session) {
        TrainingSessionDto dto = trainingSessionMapper.toDto(session);

        dto.setRegisteredCount(registrationRepository.countByTrainingSessionIdAndStatus(
                session.getId(),
                RegistrationStatus.REGISTERED
        ));

        dto.setWaitlistCount(registrationRepository.countByTrainingSessionIdAndStatus(
                session.getId(),
                RegistrationStatus.WAITLIST
        ));

        return dto;
    }

    private TrainingSessionCalendarDto toCalendarDtoWithCounts(TrainingSession session) {
        TrainingSessionCalendarDto dto = trainingSessionMapper.toCalendarDto(session);

        dto.setRegisteredCount(registrationRepository.countByTrainingSessionIdAndStatus(
                session.getId(),
                RegistrationStatus.REGISTERED
        ));

        return dto;
    }

}