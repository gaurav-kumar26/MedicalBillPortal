package com.medical.medicalbillportal.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClaimDTO {

	private Long id;

	private Long employeeId;

	private LocalDate claimDate;

	private Double totalAmount;

	private Double approvedAmount;

	private String status;

	private String billPath;

	private String remarks;
}