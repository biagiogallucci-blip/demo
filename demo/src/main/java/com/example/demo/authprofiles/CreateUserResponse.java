package com.example.demo.authprofiles;

import java.time.Instant;

public class CreateUserResponse extends UserDto{

	private Instant createdAt;
	
	public CreateUserResponse(Long id, String firstName, String lastName, String taxCode, String email, String mobile, Instant createdAt) {
		super(id, firstName, lastName, taxCode, email, mobile);
		this.createdAt = createdAt;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}
}