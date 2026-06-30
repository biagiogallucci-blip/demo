package com.example.demo.authprofiles;

public class ImportResult {

	private Long id;
    private String taxCode;
    private String status;

    public ImportResult(Long id, String taxCode, String status) {
        this.id = id;
        this.taxCode = taxCode;
        this.status = status;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
		return "ImportResult [id=" + id + ", taxCode=" + taxCode + ", status=" + status + "]";
	}
}