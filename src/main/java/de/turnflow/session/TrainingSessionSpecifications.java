package de.turnflow.session;

import de.turnflow.session.entity.TrainingSession;
import de.turnflow.session.entity.TrainingSessionStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;

public final class TrainingSessionSpecifications {

    private TrainingSessionSpecifications() {
    }

    public static Specification<TrainingSession> hasGroupId(Long groupId) {
        return (root, query, cb) -> groupId == null
                ? cb.conjunction()
                : cb.equal(root.get("trainingGroup").get("id"), groupId);
    }

    public static Specification<TrainingSession> hasStatus(TrainingSessionStatus status) {
        return (root, query, cb) -> status == null
                ? cb.conjunction()
                : cb.equal(root.get("status"), status);
    }

    public static Specification<TrainingSession> endsAfterOrAt(OffsetDateTime from) {
        return (root, query, cb) -> from == null
                ? cb.conjunction()
                : cb.greaterThanOrEqualTo(root.get("endTime"), from);
    }

    public static Specification<TrainingSession> startsBeforeOrAt(OffsetDateTime to) {
        return (root, query, cb) -> to == null
                ? cb.conjunction()
                : cb.lessThanOrEqualTo(root.get("startTime"), to);
    }
}