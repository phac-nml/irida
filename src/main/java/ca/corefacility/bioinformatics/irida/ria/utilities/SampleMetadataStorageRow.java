package ca.corefacility.bioinformatics.irida.ria.utilities;

import java.util.Map;
import java.util.Objects;

/**
 * Used to store information relating to sample metadata rows during upload.
 */
public class SampleMetadataStorageRow {

	private Map<String, String> entry;
	private Long foundSampleId;
	private String error;
	private Boolean isSaved;

	public SampleMetadataStorageRow(Map<String, String> entry) {
		this.entry = entry;
	}

	public Map<String, String> getEntry() {
		return entry;
	}

	/**
	 * Returns the associated value to which the given key is mapped
	 *
	 * @param key of the map
	 * @return the value associated with the key
	 */
	public String getEntryValue(String key) {
		return entry.get(key);
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

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Boolean isSaved() {
		return isSaved;
	}

	public void setSaved(Boolean saved) {
		isSaved = saved;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		SampleMetadataStorageRow that = (SampleMetadataStorageRow) o;
		return Objects.equals(entry, that.entry) && Objects.equals(foundSampleId, that.foundSampleId);
	}
}
