package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.list;

import java.util.List;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;

public class PagedListResponse extends AjaxResponse {
	private final long total;
	private final List<ListItem> content;

	public PagedListResponse(long total, List<ListItem> content) {
		this.total = total;
		this.content = content;
	}

	public long getTotal() {
		return total;
	}

	public List<ListItem> getContent() {
		return content;
	}
}
