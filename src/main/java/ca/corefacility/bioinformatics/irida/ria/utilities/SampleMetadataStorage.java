package ca.corefacility.bioinformatics.irida.ria.utilities;

import java.util.List;
import java.util.Map;

/**
 * Used to store information relating to sample metadata during upload.
 */
public class SampleMetadataStorage {
	private String sampleNameColumn;
	private List<String> headers;
	private List<Map<String, String>> rows;
	private List<Map<String, String>> found;
	private List<Map<String, String>> missing;

	public void setSampleNameColumn(String sampleColumnName) {
		this.sampleNameColumn = sampleColumnName;
	}

	/**
	 * Save the given headers
	 * @param headers the headers to save
	 */
	public void saveHeaders(List<String> headers) {
		this.headers = headers;
	}

	/**
	 * Save the given rows
	 * @param rows the rows to save
	 */
	public void saveRows(List<Map<String, String>> rows) {
		this.rows = rows;
	}

	/**
	 * Save the found values
	 * @param found the found values
	 */
	public void saveFound(List<Map<String, String>> found) {
		this.found = found;
	}

	/**
	 * Save the missing values
	 * @param missing the missing values
	 */
	public void saveMissing(List<Map<String, String>> missing) {
		this.missing = missing;
	}

	public String getSampleNameColumn() {
		return sampleNameColumn;
	}

	public List<String> getHeaders() {
		return headers;
	}

	public List<Map<String, String>> getRows() {
		return rows;
	}

	public List<Map<String, String>> getFound() {
		return found;
	}

	public List<Map<String, String>> getMissing() {
		return missing;
	}

	/**
	 * remove all rows
	 */
	public void removeRows() {
		this.rows = null;
	}
}
