package com.medical.medicalbillportal.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeDTO {

	private Long id;

	private String employeeCode;

	private String name;

	private LocalDate dob;

	private String department;

	private String designation;

	private String bankAccount;

	private String ifscCode;

	private Double yearlyLimit;

	private Double totalClaimed;

	private Long userId;
}