package de.turnflow.session;

import de.turnflow.common.exception.BusinessException;
import de.turnflow.common.exception.ErrorCode;
import de.turnflow.session.dto.CreateTrainingSessionRequest;
import de.turnflow.session.dto.UpdateTrainingSessionRequest;
import de.turnflow.session.entity.TrainingSession;
import de.turnflow.session.entity.TrainingSessionStatus;
import de.turnflow.support.BaseApiIT;
import de.turnflow.traininggroup.TrainingGroupRepository;
import de.turnflow.traininggroup.entity.TrainingGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class TrainingSessionServiceIT extends BaseApiIT {

    @Autowired
    private TrainingSessionService trainingSessionService;

    @Autowired
    private TrainingSessionRepository trainingSessionRepository;

    @Autowired
    private TrainingGroupRepository trainingGroupRepository;

    private TrainingGroup group;

    @BeforeEach
    void setUp() {
        trainingSessionRepository.deleteAll();
        trainingGroupRepository.deleteAll();

        group = trainingGroupRepository.save(TrainingGroup.builder()
                .name("Wettkampfgruppe")
                .description("Testgruppe")
                .active(true)
                .build());
    }

    @Test
    void create_shouldCreateTrainingSession() {
        CreateTrainingSessionRequest request = new CreateTrainingSessionRequest();
        request.setTrainingGroupId(group.getId());
        request.setTitle("Mittwochstraining");
        request.setLocation("Turnhalle Nord");
        request.setStartTime(OffsetDateTime.parse("2026-05-06T17:00:00+02:00"));
        request.setEndTime(OffsetDateTime.parse("2026-05-06T19:00:00+02:00"));
        request.setRegistrationDeadline(OffsetDateTime.parse("2026-05-06T12:00:00+02:00"));
        request.setMaxParticipants(20);
        request.setWaitlistEnabled(true);
        request.setStatus(TrainingSessionStatus.OPEN);

        var result = trainingSessionService.create(request);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getTrainingGroupId()).isEqualTo(group.getId());
        assertThat(result.getTitle()).isEqualTo("Mittwochstraining");
        assertThat(result.getRegisteredCount()).isZero();
        assertThat(result.getWaitlistCount()).isZero();
    }

    @Test
    void update_shouldThrowBusinessException_whenEndTimeBeforeExistingStartTime() {
        TrainingSession session = createSession();

        UpdateTrainingSessionRequest request = new UpdateTrainingSessionRequest();
        request.setEndTime(OffsetDateTime.parse("2026-05-06T16:00:00+02:00"));

        assertThatThrownBy(() -> trainingSessionService.update(session.getId(), request))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException businessException = (BusinessException) ex;
                    assertThat(businessException.getErrorCode())
                            .isEqualTo(ErrorCode.TRAINING_SESSION_INVALID_TIME_RANGE);
                });
    }

    @Test
    void update_shouldThrowBusinessException_whenRegistrationDeadlineAfterStartTime() {
        TrainingSession session = createSession();

        UpdateTrainingSessionRequest request = new UpdateTrainingSessionRequest();
        request.setRegistrationDeadline(OffsetDateTime.parse("2026-05-06T18:00:00+02:00"));

        assertThatThrownBy(() -> trainingSessionService.update(session.getId(), request))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException businessException = (BusinessException) ex;
                    assertThat(businessException.getErrorCode())
                            .isEqualTo(ErrorCode.TRAINING_SESSION_INVALID_REGISTRATION_DEADLINE);
                });
    }

    @Test
    void update_shouldUpdatePartialFields() {
        TrainingSession session = createSession();

        UpdateTrainingSessionRequest request = new UpdateTrainingSessionRequest();
        request.setTitle("Geändertes Training");
        request.setMaxParticipants(25);

        var result = trainingSessionService.update(session.getId(), request);

        assertThat(result.getTitle()).isEqualTo("Geändertes Training");
        assertThat(result.getMaxParticipants()).isEqualTo(25);
        assertThat(result.getStartTime()).isEqualTo(session.getStartTime());
        assertThat(result.getEndTime()).isEqualTo(session.getEndTime());
    }

    @Test
    void updateStatus_shouldChangeStatus() {
        TrainingSession session = createSession();

        var request = new de.turnflow.session.dto.UpdateTrainingSessionStatusRequest();
        request.setStatus(TrainingSessionStatus.CANCELLED);

        var result = trainingSessionService.updateStatus(session.getId(), request);

        assertThat(result.getStatus()).isEqualTo(TrainingSessionStatus.CANCELLED);
    }

    private TrainingSession createSession() {
        return trainingSessionRepository.save(TrainingSession.builder()
                .trainingGroup(group)
                .title("Mittwochstraining")
                .description("Test")
                .location("Turnhalle Nord")
                .startTime(OffsetDateTime.parse("2026-05-06T17:00:00+02:00"))
                .endTime(OffsetDateTime.parse("2026-05-06T19:00:00+02:00"))
                .registrationDeadline(OffsetDateTime.parse("2026-05-06T12:00:00+02:00"))
                .maxParticipants(20)
                .waitlistEnabled(true)
                .status(TrainingSessionStatus.OPEN)
                .build());
    }
}