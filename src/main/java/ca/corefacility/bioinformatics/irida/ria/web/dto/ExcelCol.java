package ca.corefacility.bioinformatics.irida.ria.web.dto;

/**
 * Used as a response for encapsulating excel column data
 */

public class ExcelCol {
	private int dataIndex;
	private String value;

	public ExcelCol(int dataIndex, String value) {
		this.dataIndex = dataIndex;
		this.value = value;
	}

	public int getDataIndex() {
		return dataIndex;
	}

	public void setDataIndex(int dataIndex) {
		this.dataIndex = dataIndex;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
