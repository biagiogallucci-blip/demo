package com.example.demo.authprofiles;

public class UserErrorRecord extends UserCsvRecord {

    private String issue;

	public String getIssue() {
		return issue;
	}

	public void setIssue(String issue) {
		this.issue = issue;
	}

	@Override
	public String toString() {
		return "UserErrorRecord [issue=" + issue + "]";
	}
}