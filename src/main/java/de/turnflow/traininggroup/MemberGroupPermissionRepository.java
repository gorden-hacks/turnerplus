package de.turnflow.traininggroup;

import de.turnflow.traininggroup.entity.MemberGroupPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface MemberGroupPermissionRepository extends JpaRepository<MemberGroupPermission, Long> {

    @Query("""
            select count(p) > 0
            from MemberGroupPermission p
            where p.member.id = :memberId
              and p.trainingGroup.id = :groupId
              and p.active = true
              and p.validFrom <= :today
              and (p.validTo is null or p.validTo >= :today)
            """)
    boolean hasValidPermission(Long memberId, Long groupId, LocalDate today);
}