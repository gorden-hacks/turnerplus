package de.turnflow.session;

import de.turnflow.session.entity.TrainingSession;
import de.turnflow.session.entity.TrainingSessionStatus;
import de.turnflow.traininggroup.entity.MemberGroupPermission;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
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
    public static Specification<TrainingSession> visibleForMember(Long memberId) {
        return (root, query, cb) -> {
            var subquery = query.subquery(Long.class);
            var permission = subquery.from(MemberGroupPermission.class);

            subquery.select(cb.literal(1L))
                    .where(
                            cb.equal(permission.get("member").get("id"), memberId),
                            cb.equal(permission.get("trainingGroup").get("id"), root.get("trainingGroup").get("id")),
                            cb.isTrue(permission.get("active")),
                            cb.lessThanOrEqualTo(permission.get("validFrom"), LocalDate.now()),
                            cb.or(
                                    cb.isNull(permission.get("validTo")),
                                    cb.greaterThanOrEqualTo(permission.get("validTo"), LocalDate.now())
                            )
                    );

            return cb.exists(subquery);
        };
    }
}