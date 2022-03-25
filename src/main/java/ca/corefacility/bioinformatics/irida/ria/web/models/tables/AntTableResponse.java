package ca.corefacility.bioinformatics.irida.ria.web.models.tables;

import java.util.List;

public class AntTableResponse {
	private List<?> content;
	private Long total;

	public AntTableResponse(List<?> content, Long total) {
		this.content = content;
		this.total = total;
	}

	public List<?> getContent() {
		return content;
	}

	public void setContent(List<?> content) {
		this.content = content;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}
}
