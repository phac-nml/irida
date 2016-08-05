package ca.corefacility.bioinformatics.irida.ria.web.components.datatables;

import java.util.Date;

import org.springframework.data.domain.Sort;

import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models.ProjectSampleModel;

import com.github.dandelion.datatables.core.ajax.ColumnDef;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;
import com.google.common.base.Strings;

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
	final String name;
	final Date minDate;
	final Date endDate;

	public ProjectSamplesDatatableUtils(DatatablesCriterias criterias, String name, Long minDate, Long endDate) {
		this.currentPage = getCurrentPage(criterias);
		this.pageSize = criterias.getLength();
		this.search = criterias.getSearch();
		this.filter = new ProjectSamplesFilterCriteria(criterias.getColumnDefs());
		this.name = Strings.isNullOrEmpty(name) ? null : name;
		this.minDate = minDate == null ? null : new Date(minDate);
		this.endDate = endDate == null ? null : new Date(endDate);

		ColumnDef sortedColumn = criterias.getSortedColumnDefs().get(0);
		this.sortDirection = generateSortDirection(sortedColumn);
		this.sortName = ProjectSampleModel.generateSortName(sortedColumn.getName());
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

	public String getSortProperty() {
		return sortName;
	}

	public ProjectSamplesFilterCriteria getFilter() {
		return filter;
	}

	public String getSearch() {
		return search;
	}

	public String getName() { return name; }

	public Date getMinDate() { return minDate; }

	public Date getEndDate() { return endDate; }
}
