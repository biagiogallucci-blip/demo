package com.example.demo.authprofiles;

import java.util.List;

public class BulkDeleteValidateResponse {
	private Summary summary;
    private List<UserCsvRecord> validRecords;
    private List<UserErrorRecord> errors;

	public Summary getSummary() {
		return summary;
	}

	public void setSummary(Summary summary) {
		this.summary = summary;
	}

	public List<UserCsvRecord> getValidRecords() {
		return validRecords;
	}

	public void setValidRecords(List<UserCsvRecord> validRecords) {
		this.validRecords = validRecords;
	}

	public List<UserErrorRecord> getErrors() {
		return errors;
	}

	public void setErrors(List<UserErrorRecord> errors) {
		this.errors = errors;
	}

	@Override
	public String toString() {
		return "BulkDeleteValidateResponse [summary=" + summary + ", validRecords=" + validRecords + ", errors="
				+ errors + "]";
	}
}