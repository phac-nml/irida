package ca.corefacility.bioinformatics.irida.ria.web.models.tables;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;

/**
 * Table request for an <a href="https://ant.design/components/table/">Ant Design UI Table</a>
 * <p>
 * Implements multi-column sort.
 */
public class AntTableRequest {
	private AntPagination pagination;
	private List<AntSort> order;
	private List<AntSearch> search;

	/**
	 * Build the {@link Sort} object to use in the page request.
	 * 
	 * @return an {@link Sort} object (default: unsorted)
	 */
	public Sort getSort() {
		if (order != null && order.size() > 0) {
			return Sort.by(order.stream().map(AntSort::getOrder).collect(Collectors.toList()));
		}
		return Sort.unsorted();
	}

	public int getPageSize() {
		return pagination.getPageSize();
	}

	public int getPage() {
		return pagination.getCurrent();
	}

	public void setOrder(List<AntSort> order) {
		this.order = order;
	}

	public void setPagination(AntPagination pagination) {
		this.pagination = pagination;
	}

	public List<AntSearch> getSearch() {
		return search;
	}

	public void setSearch(List<AntSearch> search) {
		this.search = search;
	}
}
