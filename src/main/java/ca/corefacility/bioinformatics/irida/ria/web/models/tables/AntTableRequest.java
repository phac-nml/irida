package ca.corefacility.bioinformatics.irida.ria.web.models.tables;

public class AntTableRequest {
	private int pageSize;
	private int current;

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getCurrent() {
		// Returning -1 since ant sends at index 1;
		return current - 1;
	}
}
