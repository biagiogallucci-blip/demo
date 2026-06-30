package com.example.demo.authprofiles;

import java.util.List;

public class BulkDeleteRequest {
	
	private List<String> usersToDelete;

	public List<String> getUsersToDelete() {
		return usersToDelete;
	}

	public void setUsersToDelete(List<String> usersToDelete) {
		this.usersToDelete = usersToDelete;
	}

	@Override
	public String toString() {
		return "BulkDeleteRequest [usersToDelete=" + usersToDelete + "]";
	}
}