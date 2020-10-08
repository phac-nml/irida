package ca.corefacility.bioinformatics.irida.ria.web.dto;

/**
 * Used as a response for encapsulating excel header column data
 */

public class ExcelHeader {
	private String title;
	private int dataIndex;
	private String key;

	public ExcelHeader(String title, int dataIndex, String key) {
		this.title = title;
		this.dataIndex = dataIndex;
		this.key = key;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getDataIndex() {
		return dataIndex;
	}

	public void setDataIndex(int dataIndex) {
		this.dataIndex = dataIndex;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
