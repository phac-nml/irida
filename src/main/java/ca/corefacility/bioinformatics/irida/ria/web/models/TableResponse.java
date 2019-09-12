package ca.corefacility.bioinformatics.irida.ria.web.models;

import java.util.List;

public class TableResponse {
	private List<TableModel> dataSource;
	private Long total;

	public TableResponse(List<TableModel> analyses, Long total) {
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
