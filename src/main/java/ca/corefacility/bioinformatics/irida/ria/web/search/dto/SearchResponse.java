package ca.corefacility.bioinformatics.irida.ria.web.search.dto;

import java.util.List;

public class SearchResponse {
	private int total;
	private List<SearchItem> items;

	public SearchResponse(int total, List<SearchItem> items) {
		this.total = total;
		this.items = items;
	}

	public int getTotal() {
		return total;
	}

	public List<SearchItem> getItems() {
		return items;
	}
}
