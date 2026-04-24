package com.medical.medicalbillportal.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ClaimHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private Claim claim;

	private String oldStatus;
	private String newStatus;

	private String changedBy; // RECEPTION / MEDICAL / FINANCE

	@Column(length = 2000)
	private String remarks;

	private LocalDateTime changedAt;

	@PrePersist
	public void setTime() {
		this.changedAt = LocalDateTime.now();
	}
}