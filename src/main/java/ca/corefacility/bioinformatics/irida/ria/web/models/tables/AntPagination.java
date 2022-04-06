package ca.corefacility.bioinformatics.irida.ria.web.models.tables;

public class AntPagination {
	private int current;
	private int pageSize;

	public int getCurrent() {
		// Returning -1 since ant sends at index 1;
		return current -1;
	}

	public int getPageSize() {
		return pageSize;
	}
}
