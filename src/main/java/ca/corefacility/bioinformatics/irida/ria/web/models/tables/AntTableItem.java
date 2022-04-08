package ca.corefacility.bioinformatics.irida.ria.web.models.tables;

/**
 * Base class to be used to represent a row in a AntD table.
 */
abstract public class AntTableItem {
	private final Long key;

	public AntTableItem(Long key) {
		this.key = key;
	}

	public Long getKey() {
		return key;
	}
}
