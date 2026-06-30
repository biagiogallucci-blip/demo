package com.example.demo.authprofiles;

public class CreateUserRequest {
	
	    private String firstName;
	    private String lastName;
	    private String taxCode;
	    private String email;
	    private String mobile;
		public String getFirstName() {
			return firstName;
		}
		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}
		public String getLastName() {
			return lastName;
		}
		public void setLastName(String lastName) {
			this.lastName = lastName;
		}
		public String getTaxCode() {
			return taxCode;
		}
		public void setTaxCode(String taxCode) {
			this.taxCode = taxCode;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getMobile() {
			return mobile;
		}
		public void setMobile(String mobile) {
			this.mobile = mobile;
		}
		@Override
		public String toString() {
			return "CreateUserRequest [firstName=" + firstName + ", lastName=" + lastName + ", taxCode=" + taxCode
					+ ", email=" + email + ", mobile=" + mobile + "]";
		}
}