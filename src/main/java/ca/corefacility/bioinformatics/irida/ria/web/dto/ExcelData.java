package ca.corefacility.bioinformatics.irida.ria.web.dto;

import java.util.List;

/**
 * Used as a response for encapsulating parsed excel file data
 */


public class ExcelData {
	private List<ExcelHeader> excelHeaders;
	private List<ExcelRow> excelRows;
	private List<String> excelSheetNames;
	private boolean parseError;

	public ExcelData(List<ExcelHeader> excelHeaders, List<ExcelRow> excelRows, List<String> excelSheetNames, boolean parseError) {
		this.excelHeaders = excelHeaders;
		this.excelRows = excelRows;
		this.excelSheetNames = excelSheetNames;
		this.parseError = parseError;
	}

	public List<ExcelHeader> getExcelHeaders() {
		return excelHeaders;
	}

	public void setExcelHeaders(List<ExcelHeader> excelHeaders) {
		this.excelHeaders = excelHeaders;
	}

	public List<ExcelRow> getExcelRows() {
		return excelRows;
	}

	public void setExcelRows(List<ExcelRow> excelRows) {
		this.excelRows = excelRows;
	}

	public List<String> getExcelSheetNames() {
		return excelSheetNames;
	}

	public void setExcelSheetNames(List<String> excelSheetNames) {
		this.excelSheetNames = excelSheetNames;
	}

	public boolean isParseError() {
		return parseError;
	}

	public void setParseError(boolean parseError) {
		this.parseError = parseError;
	}
}
