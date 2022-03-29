package ca.corefacility.bioinformatics.irida.ria.web.linelist.dto;

import java.util.List;

/**
 * Response for linelist to include total count
 */
public class EntriesResponse {
	private final long total;
	private final List<UISampleMetadata> content;

	public EntriesResponse(long total, List<UISampleMetadata> content) {
		this.total = total;
		this.content = content;
	}

	public long getTotal() {
		return total;
	}

	public List<UISampleMetadata> getContent() {
		return content;
	}
}