package ca.corefacility.bioinformatics.irida.ria.web.models.tables;

abstract public class AntTableItem {
	private String key;

	public AntTableItem(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}
