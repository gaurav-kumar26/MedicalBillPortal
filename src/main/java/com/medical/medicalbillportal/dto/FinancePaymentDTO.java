package com.medical.medicalbillportal.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinancePaymentDTO {

	private Long id;

	private Long claimId;

	private BigDecimal paymentAmount;

	private LocalDate paymentDate;

	private Long processedByUserId;
}