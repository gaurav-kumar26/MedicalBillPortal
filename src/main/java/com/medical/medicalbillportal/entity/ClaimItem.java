package com.medical.medicalbillportal.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

	private int quantity;
	private double price;

	private int approvedQuantity;

	private String status; // APPROVED / REJECTED / PARTIAL

	private String reason;

	@ManyToOne
	private Claim claim;
}