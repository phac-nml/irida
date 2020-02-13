package ca.corefacility.bioinformatics.irida.ria.web.models.tables;

import java.util.List;

/**
 * Response sent when items are requested for a table.
 */
public class TableResponse<T extends TableModel> {
	private List<T> dataSource;
	private Long total;

	public TableResponse(List<T> dataSource, Long total) {
		this.dataSource = dataSource;
		this.total = total;
	}

	public List<T> getDataSource() {
		return dataSource;
	}

	public Long getTotal() {
		return total;
	}
}
