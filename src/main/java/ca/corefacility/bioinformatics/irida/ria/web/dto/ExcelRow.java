package ca.corefacility.bioinformatics.irida.ria.web.dto;

import java.util.List;

/**
 * Used as a response for encapsulating excel parsed row data
 */

public class ExcelRow {
	private List<ExcelCol> excelCols;
	private int index;
	private int key;

	public ExcelRow(List<ExcelCol> excelCols, int index, int key) {
		this.excelCols = excelCols;
		this.index = index;
		this.key = key;
	}

	public List<ExcelCol> getExcelCols() {
		return excelCols;
	}

	public void setExcelCols(List<ExcelCol> excelCols) {
		this.excelCols = excelCols;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}
}
