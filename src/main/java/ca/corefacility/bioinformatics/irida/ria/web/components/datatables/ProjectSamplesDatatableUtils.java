package ca.corefacility.bioinformatics.irida.ria.web.components.datatables;

import org.springframework.data.domain.Sort;

import com.github.dandelion.datatables.core.ajax.ColumnDef;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;

/**
 * This is a utilities class for the Projects Samples Datatable.
 * The purpose is to change the {@link DatatablesCriterias} into a more
 * usable form.
 */
public class ProjectSamplesDatatableUtils extends DatatablesUtils {
	final int currentPage;
	final int pageSize;
	final Sort.Direction sortDirection;
	final String sortName;
	final ProjectSamplesFilterCriteria filter;
	final String search;

	public ProjectSamplesDatatableUtils(DatatablesCriterias criterias) {
		this.currentPage = getCurrentPage(criterias);
		this.pageSize = criterias.getLength();
		this.search = criterias.getSearch();
		this.filter = new ProjectSamplesFilterCriteria(criterias.getColumnDefs());

		ColumnDef sortedColumn = criterias.getSortedColumnDefs().get(0);
		this.sortDirection = generateSortDirection(sortedColumn);
		this.sortName = sortedColumn.getName();
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public Sort.Direction getSortDirection() {
		return sortDirection;
	}

	public String[] getSortProperties() {
		return new String[]{sortName};
	}

	public ProjectSamplesFilterCriteria getFilter() {
		return filter;
	}

	public String getSearch() {
		return search;
	}
}
