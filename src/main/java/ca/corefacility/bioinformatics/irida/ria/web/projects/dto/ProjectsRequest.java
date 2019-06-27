package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

import org.springframework.data.domain.Sort;

/**
 * Handles the conversion of the HttpRequestBody into an object.
 * This specifically has information need to handle the paging for the
 * Projects listing table in the UI - filter, search, sort.
 */
public class ProjectsRequest {
	private String search;
	private int current;
	private int pageSize;
	private String sortField;
	String sortDirection;

	public ProjectsRequest() {
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

	public void setSortDirection(String sortDirection) {
		this.sortDirection = sortDirection;
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
		Sort.Direction direction = this.sortDirection.equals("ascend") ? Sort.Direction.ASC : Sort.Direction.DESC;
		return new Sort(new Sort.Order(direction, this.sortField));
	}
}
