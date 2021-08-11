package ca.corefacility.bioinformatics.irida.ria.utilities;

import java.util.Map;

/**
 * Used to store information relating to sample metadata rows during upload.
 */
public class SampleMetadataStorageRow {

	private Map<String, String> entry;
	private Long foundSampleId;

	public SampleMetadataStorageRow(Map<String, String> entry) {
		this.entry = entry;
	}

	public Map<String, String> getEntry() {
		return entry;
	}

	public String getEntryValue(String name) {
		return entry.get(name);
	}

	public void setEntry(Map<String, String> entry) {
		this.entry = entry;
	}

	public Long getFoundSampleId() {
		return foundSampleId;
	}

	public void setFoundSampleId(Long foundSampleId) {
		this.foundSampleId = foundSampleId;
	}
}
