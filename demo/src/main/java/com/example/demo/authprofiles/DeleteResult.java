package com.example.demo.authprofiles;

public class DeleteResult {
	
	private String taxCode;
	private String status;

	public DeleteResult(String taxCode, String status) {
		this.taxCode = taxCode;
		this.status = status;
	}

	public String getTaxCode() {
		return taxCode;
	}

	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "DeleteResult [taxCode=" + taxCode + ", status=" + status + "]";
	}
}