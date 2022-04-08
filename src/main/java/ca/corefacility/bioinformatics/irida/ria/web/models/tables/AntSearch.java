package ca.corefacility.bioinformatics.irida.ria.web.models.tables;

/**
 * Class to represent a single column search in a AntD Table Request.
 */
public class AntSearch {
	private String property;
	private Object value;
	private String operation;

	public String getProperty() {
		return property;
	}

	public Object getValue() {
		return value;
	}

	public String getOperation() {
		return operation;
	}
}
