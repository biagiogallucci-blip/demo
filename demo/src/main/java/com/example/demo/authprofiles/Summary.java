package com.example.demo.authprofiles;

public class Summary {
	private int totalRows;
    private int validCount;
    private int errorsCount;
	public int getTotalRows() {
		return totalRows;
	}
	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}
	public int getValidCount() {
		return validCount;
	}
	public void setValidCount(int validCount) {
		this.validCount = validCount;
	}
	public int getErrorsCount() {
		return errorsCount;
	}
	public void setErrorsCount(int errorsCount) {
		this.errorsCount = errorsCount;
	}
	@Override
	public String toString() {
		return "Summary [totalRows=" + totalRows + ", validCount=" + validCount + ", errorsCount=" + errorsCount + "]";
	}
}