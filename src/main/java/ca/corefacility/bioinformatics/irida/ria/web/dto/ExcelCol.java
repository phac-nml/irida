package ca.corefacility.bioinformatics.irida.ria.web.dto;

/**
 * Used as a response for encapsulating excel column data
 */

public class ExcelCol {
	private int dataIndex;
	private String columnType;
	private String value;
	private Double numericValue;

	/*
	If column type is text
	 */
	public ExcelCol(int dataIndex, String value) {
		this.dataIndex = dataIndex;
		this.value = value;
		this.columnType = "text";
	}

	/*
	If column type is numeric
	 */
	public ExcelCol(int dataIndex, Double numericValue) {
		this.dataIndex = dataIndex;
		this.numericValue = numericValue;
		this.columnType = "numeric";
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

	public Double getNumericValue() {
		return numericValue;
	}

	public void setNumericValue(Double numericValue) {
		this.numericValue = numericValue;
	}

	public String getColumnType() {
		return columnType;
	}

	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}
}
