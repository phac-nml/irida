package ca.corefacility.bioinformatics.irida.ria.web.models;

import java.util.List;

/**
 * Response sent when items are requested for a table.
 */
public class TableResponse {
	private List<TableModel> dataSource;
	private Long total;

	public TableResponse(List<TableModel> dataSource, Long total) {
		this.dataSource = dataSource;
		this.total = total;
	}

	public List<TableModel> getDataSource() {
		return dataSource;
	}

	public Long getTotal() {
		return total;
	}
}
