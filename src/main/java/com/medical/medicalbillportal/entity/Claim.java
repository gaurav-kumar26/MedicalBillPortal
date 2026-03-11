package com.medical.medicalbillportal.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
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

    @ManyToOne
    private Employee employee;

    private LocalDate claimDate;
    private Double totalAmount;
    private Double approvedAmount;
    private String status;
    private String billPath;

<<<<<<< HEAD
    @Column(length = 2000)
    private String remarks;
}
=======
	@Column(length = 2000)
	private String remarks;

	public void setStatus(String status) {
		this.status = status;
	}
}
>>>>>>> 52268485695fc28fd785e9cafeda62c0cf15ab23
