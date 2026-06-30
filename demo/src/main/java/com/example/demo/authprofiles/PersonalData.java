package com.example.demo.authprofiles;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "PERSONAL_DATA", schema = "XRBNPPUSR")
public class PersonalData {

    @Id
    private Long id;

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String placeOfBirth;
    private String taxCode;
    private String email;
    private String phoneNumber;
    private Integer emailValid;
    private Integer phoneValid;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public String getPlaceOfBirth() {
		return placeOfBirth;
	}
	public void setPlaceOfBirth(String placeOfBirth) {
		this.placeOfBirth = placeOfBirth;
	}
	public String getTaxCode() {
		return taxCode;
	}
	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public Integer getEmailValid() {
		return emailValid;
	}
	public void setEmailValid(Integer emailValid) {
		this.emailValid = emailValid;
	}
	public Integer getPhoneValid() {
		return phoneValid;
	}
	public void setPhoneValid(Integer phoneValid) {
		this.phoneValid = phoneValid;
	}
}
