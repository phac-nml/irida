package ca.corefacility.bioinformatics.irida.ria.web.models.tables;

abstract public class AntTableItem {
	private final Long key;

	public AntTableItem(Long key) {
		this.key = key;
	}

	public Long getKey() {
		return key;
	}
}
