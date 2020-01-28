package ca.corefacility.bioinformatics.irida.ria.web.models.tables;

import java.util.List;

/**
 * Response sent when items are requested for a table.
 */
public class TableResponse {
	private List<? extends TableModel> dataSource;
	private Long total;

	public TableResponse(List<? extends TableModel> dataSource, Long total) {
		this.dataSource = dataSource;
		this.total = total;
	}

	public List<? extends TableModel> getDataSource() {
		return dataSource;
	}

	public Long getTotal() {
		return total;
	}
}
