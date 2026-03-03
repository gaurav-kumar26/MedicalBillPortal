package com.medical.medicalbillportal.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "employees")
@Getter
@Setter
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String employeeCode;
	private String name;
	private LocalDate dob;
	private String department;
	private String designation;
	private String bankAccount;
	private String ifscCode;
	private Double yearlyLimit;
	private Double totalClaimed = 0.0;

	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;
}
