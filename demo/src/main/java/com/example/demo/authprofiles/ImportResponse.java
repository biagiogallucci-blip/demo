package com.example.demo.authprofiles;

import java.util.List;
import java.util.Map;

public class ImportResponse {
	
	private String status;
    private String message;
    private int importedCount;
    private List<ImportResult> data;
    private List<Map<String, Object>> errors;
    
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getImportedCount() {
		return importedCount;
	}
	public void setImportedCount(int importedCount) {
		this.importedCount = importedCount;
	}
	public List<ImportResult> getData() {
		return data;
	}
	public void setData(List<ImportResult> data) {
		this.data = data;
	}
	public List<Map<String, Object>> getErrors() {
		return errors;
	}
	public void setErrors(List<Map<String, Object>> errors) {
		this.errors = errors;
	}
	@Override
	public String toString() {
		return "ImportResponse [status=" + status + ", message=" + message + ", importedCount=" + importedCount
				+ ", data=" + data + ", errors=" + errors + "]";
	}
}