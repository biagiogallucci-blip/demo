package com.example.demo.authprofiles;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "USERS", schema = "XRBNPPUSR")
public class User implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "PERSONAL_DATA_ID", nullable = false)
    private PersonalData personalData;

    private String email;
    private String passwordHash;
    private Integer statusCode;
    private LocalDate setDate;
    private LocalDateTime lastLoginTime;
    private Integer errorNumber;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public PersonalData getPersonalData() {
		return personalData;
	}
	public void setPersonalData(PersonalData personalData) {
		this.personalData = personalData;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPasswordHash() {
		return passwordHash;
	}
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	public Integer getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}
	public LocalDate getSetDate() {
		return setDate;
	}
	public void setSetDate(LocalDate setDate) {
		this.setDate = setDate;
	}
	public LocalDateTime getLastLoginTime() {
		return lastLoginTime;
	}
	public void setLastLoginTime(LocalDateTime lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	public Integer getErrorNumber() {
		return errorNumber;
	}
	public void setErrorNumber(Integer errorNumber) {
		this.errorNumber = errorNumber;
	}
    
    
}