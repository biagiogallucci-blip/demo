package com.example.demo.authprofiles;

import java.util.List;

public class BulkDeleteResponse {
	private String status;
	private String message;
	private int deletedCount;
	private List<DeleteResult> data;

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

	public int getDeletedCount() {
		return deletedCount;
	}

	public void setDeletedCount(int deletedCount) {
		this.deletedCount = deletedCount;
	}

	public List<DeleteResult> getData() {
		return data;
	}

	public void setData(List<DeleteResult> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "BulkDeleteResponse [status=" + status + ", message=" + message + ", deletedCount=" + deletedCount
				+ ", data=" + data + "]";
	}
}