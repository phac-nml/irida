package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

import java.util.List;

import ca.corefacility.bioinformatics.irida.ria.web.models.TableModel;

/**
 * UI Response for the current page of the Analyses Table.
 */
public class AnalysesListResponse {
	private List<TableModel> dataSource;
	private Long total;

	public AnalysesListResponse(List<TableModel> analyses, Long total) {
		this.dataSource = analyses;
		this.total = total;
	}

	public List<TableModel> getDataSource() {
		return dataSource;
	}

	public Long getTotal() {
		return total;
	}
}
