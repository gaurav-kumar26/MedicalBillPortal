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

    @Column(length = 2000)
    private String remarks;
}