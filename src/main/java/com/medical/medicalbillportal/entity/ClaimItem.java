package com.medical.medicalbillportal.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ClaimItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String itemName; // Tablet / Operation

	// Enterprise fields (required by new UI)
	private Double amount; // Total amount for this line item

	@Enumerated(EnumType.STRING)
	private ClaimItemType type; // MEDICINE / OPERATION

	private boolean covered; // Covered by policy

	// Legacy fields (kept for backward compatibility with existing UI/templates/services)
	private int quantity;
	private double price;

	private int approvedQuantity;

	@Enumerated(EnumType.STRING)
	private ItemStatus status; // APPROVED / REJECTED / PARTIAL

	private String reason;

	@ManyToOne
	private Claim claim;
}