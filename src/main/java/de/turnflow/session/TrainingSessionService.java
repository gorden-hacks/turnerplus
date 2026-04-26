package de.turnflow.session;

import de.turnflow.common.dto.PageResponse;
import de.turnflow.common.exception.BusinessException;
import de.turnflow.common.exception.ErrorCode;
import de.turnflow.common.exception.NotFoundException;
import de.turnflow.common.mapper.PageMapper;
import de.turnflow.registration.RegistrationCountProjection;
import de.turnflow.registration.RegistrationRepository;
import de.turnflow.registration.entity.Registration;
import de.turnflow.registration.entity.RegistrationStatus;
import de.turnflow.session.dto.*;
import de.turnflow.session.entity.TrainingSession;
import de.turnflow.session.entity.TrainingSessionStatus;
import de.turnflow.session.mapper.TrainingSessionMapper;
import de.turnflow.session.mapper.TrainingSessionProjectionMapper;
import de.turnflow.traininggroup.TrainingGroupRepository;
import de.turnflow.traininggroup.entity.TrainingGroup;
import de.turnflow.user.UserRepository;
import de.turnflow.user.UserService;
import de.turnflow.user.entity.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.IsoFields;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TrainingSessionService {

    private final TrainingSessionRepository trainingSessionRepository;
    private final TrainingGroupRepository trainingGroupRepository;
    private final RegistrationRepository registrationRepository;
    private final TrainingSessionMapper trainingSessionMapper;
    private final UserRepository userRepository;
    private final TrainingSessionProjectionMapper projectionMapper;

    @Value("${app.timezone:Europe/Berlin}")
    private String appTimezone;

    @Transactional(readOnly = true)
    public PageResponse<TrainingSessionDto> findFiltered(
            Long groupId,
            TrainingSessionStatus status,
            OffsetDateTime from,
            OffsetDateTime to,
            Pageable pageable
    ) {
        Specification<TrainingSession> spec = Specification
                .where(TrainingSessionSpecifications.hasGroupId(groupId))
                .and(TrainingSessionSpecifications.hasStatus(status))
                .and(TrainingSessionSpecifications.endsAfterOrAt(from))
                .and(TrainingSessionSpecifications.startsBeforeOrAt(to));

        Page<TrainingSession> sessionsPage = trainingSessionRepository.findAll(spec, pageable);

        Map<Long, RegistrationCountProjection> countsBySessionId = loadCounts(sessionsPage.getContent());

        Page<TrainingSessionDto> dtoPage = sessionsPage.map(session -> {
            TrainingSessionDto dto = trainingSessionMapper.toDto(session);

            RegistrationCountProjection counts = countsBySessionId.get(session.getId());

            dto.setRegisteredCount(counts != null ? counts.getRegisteredCount() : 0);
            dto.setWaitlistCount(counts != null ? counts.getWaitlistCount() : 0);

            return dto;
        });

        return PageMapper.toPageResponse(dtoPage);
    }

    private Map<Long, RegistrationCountProjection> loadCounts(List<TrainingSession> sessions) {
        List<Long> sessionIds = sessions.stream()
                .map(TrainingSession::getId)
                .toList();

        if (sessionIds.isEmpty()) {
            return Map.of();
        }

        return registrationRepository.countByTrainingSessionIds(
                        sessionIds,
                        RegistrationStatus.REGISTERED,
                        RegistrationStatus.WAITLIST
                )
                .stream()
                .collect(Collectors.toMap(
                        RegistrationCountProjection::getTrainingSessionId,
                        projection -> projection
                ));
    }


    @Transactional(readOnly = true)
    public TrainingSessionDto findById(Long id) {
        TrainingSession session = getSession(id);
        return toDtoWithCounts(session);
    }

    @Transactional(readOnly = true)
    public List<MyTrainingSessionDto> findMyTrainingSessions(
            Long userId,
            OffsetDateTime from,
            OffsetDateTime to
    ) {
        UserAccount user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND, userId));

        if (user.getMember() == null) {
            throw new BusinessException(ErrorCode.USER_NOT_LINKED_TO_MEMBER);
        }

        Long memberId = user.getMember().getId();

        List<TrainingSession> sessions =
                trainingSessionRepository.findVisibleForMember(memberId, from, to);

        List<Long> sessionIds = sessions.stream()
                .map(TrainingSession::getId)
                .toList();

        Map<Long, RegistrationStatus> myStatuses = registrationRepository
                .findByMemberIdAndTrainingSessionIdIn(memberId, sessionIds)
                .stream()
                .collect(Collectors.toMap(
                        r -> r.getTrainingSession().getId(),
                        Registration::getStatus
                ));

        Map<Long, RegistrationCountProjection> countsBySessionId = loadCounts(sessions);

        return sessions.stream()
                .map(session -> {
                    RegistrationCountProjection counts = countsBySessionId.get(session.getId());

                    return MyTrainingSessionDto.builder()
                            .id(session.getId())
                            .trainingGroupId(session.getTrainingGroup().getId())
                            .trainingGroupName(session.getTrainingGroup().getName())
                            .title(session.getTitle())
                            .location(session.getLocation())
                            .startTime(session.getStartTime())
                            .endTime(session.getEndTime())
                            .registrationDeadline(session.getRegistrationDeadline())
                            .maxParticipants(session.getMaxParticipants())
                            .registeredCount(counts != null ? counts.getRegisteredCount() : 0)
                            .waitlistCount(counts != null ? counts.getWaitlistCount() : 0)
                            .status(session.getStatus())
                            .myRegistrationStatus(myStatuses.get(session.getId()))
                            .build();
                })
                .toList();
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

        validateUpdateRequest(session, request);

        updateTrainingGroupIfPresent(session, request.getTrainingGroupId());

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
        Specification<TrainingSession> spec = Specification
                .where(TrainingSessionSpecifications.hasGroupId(groupId))
                .and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("startTime"), from))
                .and((root, query, cb) -> cb.lessThan(root.get("startTime"), to));

        List<TrainingSession> sessions = trainingSessionRepository.findAll(
                spec,
                Sort.by(Sort.Direction.ASC, "startTime")
        );

        Map<Long, RegistrationCountProjection> countsBySessionId = loadCounts(sessions);

        return sessions.stream()
                .map(session -> {
                    TrainingSessionCalendarDto dto = trainingSessionMapper.toCalendarDto(session);

                    RegistrationCountProjection counts = countsBySessionId.get(session.getId());
                    dto.setRegisteredCount(counts != null ? counts.getRegisteredCount() : 0);

                    return dto;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public TrainingSessionWeekCalendarDto weekCalendar(LocalDate weekStart, Long groupId) {
        LocalDate weekEnd = weekStart.plusDays(6);

        ZoneId zoneId = ZoneId.of("Europe/Berlin");

        OffsetDateTime from = weekStart.atStartOfDay(zoneId).toOffsetDateTime();
        OffsetDateTime to = weekEnd.plusDays(1).atStartOfDay(zoneId).toOffsetDateTime();

        List<TrainingSessionCalendarDto> sessions = calendar(from, to, groupId);

        List<TrainingSessionCalendarDayDto> days = weekStart
                .datesUntil(weekEnd.plusDays(1))
                .map(date -> TrainingSessionCalendarDayDto.builder()
                        .date(date)
                        .sessions(sessions.stream()
                                .filter(session -> session.getStartTime().toLocalDate().equals(date))
                                .toList())
                        .build())
                .toList();

        return TrainingSessionWeekCalendarDto.builder()
                .weekStart(weekStart)
                .weekEnd(weekEnd)
                .groupId(groupId)
                .days(days)
                .build();
    }

    @Transactional(readOnly = true)
    public TrainingSessionWeekCalendarDto isoWeekCalendar(int year, int week, Long groupId) {
        LocalDate weekStart = LocalDate
                .of(year, 1, 4)
                .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week)
                .with(ChronoField.DAY_OF_WEEK, 1);

        return weekCalendar(weekStart, groupId);
    }

    private void validateUpdateRequest(
            TrainingSession session,
            UpdateTrainingSessionRequest request
    ) {
        OffsetDateTime newStart = resolveStartTime(session, request);
        OffsetDateTime newEnd = resolveEndTime(session, request);
        OffsetDateTime newRegistrationDeadline = resolveRegistrationDeadline(session, request);

        validateTimeRange(newStart, newEnd);
        validateRegistrationDeadline(newRegistrationDeadline, newStart);
    }

    private OffsetDateTime resolveStartTime(
            TrainingSession session,
            UpdateTrainingSessionRequest request
    ) {
        return request.getStartTime() != null
                ? request.getStartTime()
                : session.getStartTime();
    }

    private OffsetDateTime resolveEndTime(
            TrainingSession session,
            UpdateTrainingSessionRequest request
    ) {
        return request.getEndTime() != null
                ? request.getEndTime()
                : session.getEndTime();
    }

    private OffsetDateTime resolveRegistrationDeadline(
            TrainingSession session,
            UpdateTrainingSessionRequest request
    ) {
        return request.getRegistrationDeadline() != null
                ? request.getRegistrationDeadline()
                : session.getRegistrationDeadline();
    }

    private void validateTimeRange(OffsetDateTime start, OffsetDateTime end) {
        if (start == null || end == null) {
            return;
        }

        if (!end.isAfter(start)) {
            throw new BusinessException(ErrorCode.TRAINING_SESSION_INVALID_TIME_RANGE);
        }
    }

    private void validateRegistrationDeadline(
            OffsetDateTime registrationDeadline,
            OffsetDateTime startTime
    ) {
        if (registrationDeadline == null || startTime == null) {
            return;
        }

        if (registrationDeadline.isAfter(startTime)) {
            throw new BusinessException(
                    ErrorCode.TRAINING_SESSION_INVALID_REGISTRATION_DEADLINE
            );
        }
    }

    private void updateTrainingGroupIfPresent(
            TrainingSession session,
            Long trainingGroupId
    ) {
        if (trainingGroupId == null) {
            return;
        }

        TrainingGroup group = trainingGroupRepository.findById(trainingGroupId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.TRAINING_GROUP_NOT_FOUND,
                        trainingGroupId
                ));

        session.setTrainingGroup(group);
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

    private ZoneId appZoneId() {
        return ZoneId.of(appTimezone);
    }

}