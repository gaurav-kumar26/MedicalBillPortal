package com.medical.medicalbillportal.entity;

public enum ClaimStatus {

	PENDING("PENDING"), SUBMITTED("SUBMITTED"), RECEPTION_VERIFIED("RECEPTION_VERIFIED"), ON_HOLD("ON_HOLD"),

	// 🔥 ADD THESE
	REJECTED("REJECTED"), RECEPTION_REJECTED("RECEPTION_REJECTED"),

	MEDICAL_APPROVED("MEDICAL_APPROVED"), MEDICAL_REJECTED("MEDICAL_REJECTED"), MEDICAL_PROCESSED("MEDICAL_PROCESSED"),

	FINANCE_PAID("FINANCE_PAID"), FINANCE_REJECTED("FINANCE_REJECTED"), FINANCE_HOLD("FINANCE_HOLD");

	private final String code;

	ClaimStatus(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public static ClaimStatus fromCode(String code) {
		if (code == null) {
			return null;
		}
		for (ClaimStatus s : values()) {
			if (s.code.equalsIgnoreCase(code)) {
				return s;
			}
		}
		return null;
	}
}