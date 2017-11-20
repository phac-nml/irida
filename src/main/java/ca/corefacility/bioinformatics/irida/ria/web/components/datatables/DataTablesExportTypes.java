package ca.corefacility.bioinformatics.irida.ria.web.components.datatables;

public enum DataTablesExportTypes {
	EXCEL("excel"), CSV("csv");

	private final String value;

	private DataTablesExportTypes(String value) {
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return value;
	}
}
