package ca.corefacility.bioinformatics.irida.ria.web.admin.dto.statistics;

/**
 * Used to represent the time period and counts for statistics.
 */

public class GenericStatModel {
	private String key;
	private Long value;

	public GenericStatModel(String key, Long value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}
}
