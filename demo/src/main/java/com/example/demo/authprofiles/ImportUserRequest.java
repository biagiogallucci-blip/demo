package com.example.demo.authprofiles;

public class ImportUserRequest extends CreateUserRequest {
    private Long companyId;

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
}