package com.example.demo.authprofiles;

import java.math.BigInteger;

public class UserDto {
	
	private Long id;
    private String firstName;
    private String lastName;
    private String taxCode;
    private String email;
    private String mobile;
    
	public UserDto(Long id, String firstName, String lastName, String taxCode, String email, String mobile) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.taxCode = taxCode;
		this.email = email;
		this.mobile = mobile;
	}
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
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	@Override
	public String toString() {
		return "UserDto [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", taxCode=" + taxCode
				+ ", email=" + email + ", mobile=" + mobile + "]";
	}
}