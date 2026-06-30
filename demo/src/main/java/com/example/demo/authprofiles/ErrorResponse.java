package com.example.demo.authprofiles;

import java.util.List;

public class ErrorResponse {
	
	private String error;
	private String message;
	private List<ErrorDetail> details;

	public ErrorResponse(String error, String message, List<ErrorDetail> details) {
		this.error = error;
		this.message = message;
		this.details = details;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<ErrorDetail> getDetails() {
		return details;
	}

	public void setDetails(List<ErrorDetail> details) {
		this.details = details;
	}

	@Override
	public String toString() {
		return "ErrorResponse [error=" + error + ", message=" + message + ", details=" + details + "]";
	}
}
