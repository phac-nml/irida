package ca.corefacility.bioinformatics.irida.ria.web.dto;

import java.util.List;

/**
 * Used as a response for encapsulating parsed excel file data
 */


public class ExcelData {
	private List<ExcelHeader> excelHeaders;
	private List<ExcelRow> excelRows;

	public ExcelData(List<ExcelHeader> excelHeaders, List<ExcelRow> excelRows) {
		this.excelHeaders = excelHeaders;
		this.excelRows = excelRows;
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
}
