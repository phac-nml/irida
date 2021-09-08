package ca.corefacility.bioinformatics.irida.ria.utilities;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Used to store information relating to sample metadata during upload.
 */
public class SampleMetadataStorage {
	private String sampleNameColumn;
	private List<String> headers;
	private List<SampleMetadataStorageRow> rows;

	public void setSampleNameColumn(String sampleColumnName) {
		this.sampleNameColumn = sampleColumnName;
	}

	public void setHeaders(List<String> headers) {
		this.headers = headers;
	}

	public String getSampleNameColumn() {
		return sampleNameColumn;
	}

	public List<String> getHeaders() {
		return headers;
	}

	public List<SampleMetadataStorageRow> getRows() {
		return rows;
	}

	/**
	 * Returns the row from storage given the sample name and column name
	 *
	 * @param sampleName       the name of the sample
	 * @param sampleNameColumn the header name of the sample column
	 * @return the value associated with the key
	 */
	public SampleMetadataStorageRow getRow(String sampleName, String sampleNameColumn) {
		return rows.stream()
				.filter(row -> sampleName.equals(row.getEntryValue(sampleNameColumn)))
				.findFirst()
				.orElse(null);
	}

	public List<SampleMetadataStorageRow> getFoundRows() {
		return rows == null ?
				Collections.emptyList() :
				rows.stream()
						.filter((r) -> r != null && r.getFoundSampleId() != null)
						.collect(Collectors.toList());
	}

	public void setRows(List<SampleMetadataStorageRow> rows) {
		this.rows = rows;
	}

	/**
	 * remove all rows
	 */
	public void removeRows() {
		this.rows = null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		SampleMetadataStorage that = (SampleMetadataStorage) o;
		return Objects.equals(sampleNameColumn, that.sampleNameColumn) && Objects.equals(headers, that.headers)
				&& Objects.equals(rows, that.rows);
	}

}
