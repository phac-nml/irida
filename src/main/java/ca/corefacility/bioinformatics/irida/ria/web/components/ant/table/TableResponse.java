package ca.corefacility.bioinformatics.irida.ria.web.components.ant.table;

import java.util.List;

/**
 * Contains information needed for listing of items in an ag-grid table
 */
public class TableResponse {
	private List<? extends TableModel> models;
	private Long total;

	public TableResponse(List<? extends TableModel> models, long total) {
		this.models = models;
		this.total = total;
	}

	public List<? extends TableModel> getModels() {
		return models;
	}

	public Long getTotal() {
		return total;
	}
}
