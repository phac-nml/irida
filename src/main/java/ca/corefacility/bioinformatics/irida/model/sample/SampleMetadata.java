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

	@Id
	String id;

	@Field(value = "metadata")
	Map<String, Object> metadata;

	@Indexed(unique = true)
	Long sampleId;

	public SampleMetadata() {
		metadata = new HashMap<>();
	}

	public String getId() {
		return id;
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
