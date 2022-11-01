package ca.corefacility.bioinformatics.irida.ria.web.search.dto;

import ca.corefacility.bioinformatics.irida.ria.web.models.tables.AntTableRequest;

public class SearchRequest extends AntTableRequest {
	private boolean global;
	private String query;

	public boolean isGlobal() {
		return global;
	}

	public void setGlobal(boolean global) {
		this.global = global;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
}
