package ca.corefacility.bioinformatics.irida.ria.web.components.datatables;

import java.util.Date;

import org.springframework.data.domain.Sort;

import com.google.common.base.Strings;

/**
 * This is a utilities class for the Projects Samples Datatable.
 * The purpose is to change the {@link DataTablesParams} into a more
 * usable form for a project.
 */
public class ProjectSamplesDatatableUtils extends DatatablesUtils {
	final int currentPage;
	final int pageSize;
	final Sort sort;
	final String search;
	final String name;
	final Date minDate;
	final Date endDate;

	public ProjectSamplesDatatableUtils(DataTablesParams params, String name, Long minDate, Long endDate) {
		this.currentPage = params.getCurrentPage();
		this.pageSize = params.getLength();
		this.search = params.getSearchValue();
		this.name = Strings.isNullOrEmpty(name) ? null : name;
		this.minDate = minDate == null ? null : new Date(minDate);
		this.endDate = endDate == null ? null : new Date(endDate);
		this.sort = params.getSort();
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public Sort getSort() {
		return sort;
	}

	public String getSearch() {
		return search;
	}

	public String getName() { return name; }

	public Date getMinDate() { return minDate; }

	public Date getEndDate() { return endDate; }
}
