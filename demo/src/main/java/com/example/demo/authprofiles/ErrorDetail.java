package com.example.demo.authprofiles;

public class ErrorDetail {

	private String field;
	private String issue;
	private String description;

	public ErrorDetail(String field, String issue, String description) {
		this.field = field;
		this.issue = issue;
		this.description = description;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getIssue() {
		return issue;
	}

	public void setIssue(String issue) {
		this.issue = issue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "ErrorDetail [field=" + field + ", issue=" + issue + ", description=" + description + "]";
	}
}