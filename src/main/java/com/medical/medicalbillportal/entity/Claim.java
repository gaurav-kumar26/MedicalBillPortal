package com.medical.medicalbillportal.entity;

import java.time.LocalDate;
import java.util.UUID;

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

    // 🔥 NEW: Public Claim ID (for tracking)
    @Column(unique = true, nullable = false, updatable = false)
    private String claimId;

    @ManyToOne
    private Employee employee;

    private LocalDate claimDate;
    private Double totalAmount;
    private Double approvedAmount;

    private String status;

    private String billPath;

    // 🔥 NEW: GST Number
    private String gstNumber;

    @Column(length = 2000)
    private String remarks;

    // 🔥 AUTO GENERATE CLAIM ID + DEFAULT STATUS
    @PrePersist
    public void generateClaimId() {
        this.claimId = "CLM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.status = "PENDING";
    }
}