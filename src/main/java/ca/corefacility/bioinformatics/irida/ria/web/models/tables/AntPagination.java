package ca.corefacility.bioinformatics.irida.ria.web.models.tables;

/**
 * Class to represent a page request in a AntD Table Request.
 */
public class AntPagination {
	private int current;
	private int pageSize;

	public AntPagination(int current, int pageSize) {
		this.current = current;
		this.pageSize = pageSize;
	}

	/**
	 * Return the page number to request
	 *
	 * @return the page number
	 */
	public int getCurrent() {
		// Returning -1 since ant sends at index 1;
		return current - 1;
	}

	public int getPageSize() {
		return pageSize;
	}
}
