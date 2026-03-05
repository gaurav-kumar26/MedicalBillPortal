package com.medical.medicalbillportal.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicalReviewDTO {

	private Long id;

	private Long claimId;

	private BigDecimal approvedAmount;

	private String decision;

	private String notes;

	private Long reviewedByUserId;
}