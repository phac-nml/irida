package ca.corefacility.bioinformatics.irida.ria.web.components.ant.table;

import org.springframework.data.domain.Sort;

import com.google.common.base.Strings;

/**
 * Handles the conversion of the HttpRequestBody into an object.
 * This specifically has information need to handle the paging for the
 * Projects listing table in the UI - filter, search, sort.
 */
public class TableRequest {
	private String search;
	private int current;
	private int pageSize;
	private String sortField;
	String sortDirection;

	public TableRequest() {
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public String getSortField() {
		return sortField;
	}

	public void setSortDirection(String sortDirection) {
		this.sortDirection = sortDirection;
	}

	public Sort.Direction getSortDirection() {
		return Strings.isNullOrEmpty(this.sortDirection) ?
				Sort.Direction.ASC :
				this.sortDirection.equals("ascend") ? Sort.Direction.ASC : Sort.Direction.DESC;
	}

	/**
	 * Since we he need an actual {@link Sort} object and cannot pass this from
	 * the client, we create one from the information fathered from the client
	 * Direction of sort
	 * Column (attribute) of sort
	 *
	 * @return {@link Sort}
	 */
	public Sort getSort() {

		return Sort.by(new Sort.Order(getSortDirection(), this.sortField));
	}
}
