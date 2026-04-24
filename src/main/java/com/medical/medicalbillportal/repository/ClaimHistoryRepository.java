package com.medical.medicalbillportal.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.medical.medicalbillportal.entity.ClaimHistory;

public interface ClaimHistoryRepository extends JpaRepository<ClaimHistory, Long> {

	List<ClaimHistory> findByClaimIdOrderByChangedAtDesc(Long claimId);

	// All history by role (for department records)
	List<ClaimHistory> findByChangedByOrderByChangedAtDesc(String changedBy);

	// Count by role + new status + date range (for today stats)
	@Query("SELECT COUNT(h) FROM ClaimHistory h WHERE h.changedBy = :role "
			+ "AND h.newStatus = :status AND h.changedAt >= :from AND h.changedAt <= :to")
	long countByRoleAndStatusToday(@Param("role") String role, @Param("status") String status,
			@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

	// All history by role + new status (all-time)
	List<ClaimHistory> findByChangedByAndNewStatus(String changedBy, String newStatus);

}