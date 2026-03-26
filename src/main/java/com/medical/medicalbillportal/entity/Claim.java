package com.medical.medicalbillportal.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "claims")
@Getter
@Setter
public class Claim {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false, updatable = false)
	private String claimId;

	@ManyToOne
	private Employee employee;

	private LocalDate claimDate;
	private Double totalAmount;
	private Double approvedAmount;

	private String status;

	private String billPath;

	private String gstNumber;

	@Column(length = 2000)
	private String remarks;

	// Finance processed timestamp (set by finance module)
	private LocalDateTime processedAt;

	// 🔥 ADD THIS
	@OneToMany(mappedBy = "claim", cascade = CascadeType.ALL)
	private List<ClaimItem> items;

	@PrePersist
	public void generateClaimId() {
		this.claimId = "CLM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
		this.status = "PENDING";
	}
}