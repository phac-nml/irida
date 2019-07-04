package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

import org.springframework.data.domain.Sort;

public class AnalysesListRequest {
	private int current;
	private int pageSize;
	private String sortColumn;
	private String sortDirection;
	private String search;
	private AnalysesFilters filters;

	public AnalysesListRequest() {}

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

	public void setSortDirection(String sortDirection) {
		this.sortDirection = sortDirection;
	}

	public void setSortColumn(String sortColumn) {
		this.sortColumn = sortColumn;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public AnalysesFilters getFilters() {
		return filters;
	}

	public void setFilters(AnalysesFilters filters) {
		this.filters = filters;
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
		return new Sort(new Sort.Order(direction, this.sortColumn));
	}
}
