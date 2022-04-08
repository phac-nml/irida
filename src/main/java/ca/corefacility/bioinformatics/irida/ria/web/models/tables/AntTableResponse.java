package ca.corefacility.bioinformatics.irida.ria.web.models.tables;

import java.util.List;

/**
 * Base class to be used to represent a single page in a AntD table.
 *
 * @param <T> The type of {@link AntTableItem} included in the response.
 */
public class AntTableResponse<T extends AntTableItem> {
	private List<T> content;
	private Long total;

	public AntTableResponse(List<T> content, Long total) {
		this.content = content;
		this.total = total;
	}

	public List<?> getContent() {
		return content;
	}

	public void setContent(List<T> content) {
		this.content = content;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}
}
