package com.medical.medicalbillportal.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "finance_payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancePayment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Many payments can belong to one claim (generally 1:1, but flexible)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "claim_id", nullable = false)
	private Claim claim;

	@Column(name = "payment_amount", nullable = false)
	private Double paymentAmount;

	@Column(name = "payment_date", nullable = false)
	private LocalDate paymentDate;

	// Finance officer who processed this payment
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "processed_by")
	private User processedBy;
}
