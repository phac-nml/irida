package ca.corefacility.bioinformatics.irida.model.sample;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Stores unstructured metadata for a given {@link Sample}.
 */
@Document
public class SampleMetadata {

	@Field(value = "metadata")
	Map<String, Object> metadata;

	@Id
	Long sampleId;

	public SampleMetadata() {
		metadata = new HashMap<>();
	}

	public Map<String, Object> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
	}

	public Long getSampleId() {
		return sampleId;
	}

	public void setSampleId(Long sampleId) {
		this.sampleId = sampleId;
	}
}
