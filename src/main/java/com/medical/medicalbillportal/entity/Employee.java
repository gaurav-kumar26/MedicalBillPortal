package com.medical.medicalbillportal.entity;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "employees")
@Getter
@Setter
public class Employee implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "employee_id", unique = true, nullable = false, length = 50)
	private String employeeId;

	/**
	 * Backward-compatible alias for existing templates/services using employeeCode.
	 * Reads from the same DB column as employeeId.
	 */
	@Column(name = "employee_id", insertable = false, updatable = false)
	private String employeeCode;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false, length = 50)
	private String role;

	@Column(nullable = false)
	private Boolean enabled = Boolean.TRUE;

	private String name;
	private LocalDate dob;
	private String department;
	private String designation;
	private String bankAccount;
	private String ifscCode;
	private Double yearlyLimit;
	private Double totalClaimed = 0.0;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		String r = role;
		if (r == null || r.isBlank()) {
			r = "ROLE_EMPLOYEE";
		} else {
			r = r.trim();
			// Fix accidental ROLE_ROLE_ prefix.
			r = r.replace("ROLE_ROLE_", "ROLE_");
			if (!r.startsWith("ROLE_")) {
				r = "ROLE_" + r;
			}
		}
		return List.of(new SimpleGrantedAuthority(r));
	}

	@Override
	public String getUsername() {
		return employeeId;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return enabled != null ? enabled : true;
	}
}