package ca.corefacility.bioinformatics.irida.ria.web.linelist.dto;

import java.util.HashMap;
import java.util.Map;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;

/**
 * Represents {@link Sample} metadata in the linelist table.
 */
public class UISampleMetadata extends HashMap<String, String> {
	public static final String PREFIX = "irida-";
	public static final String SAMPLE_NAME = PREFIX + "sample-name";
	public static final String SAMPLE_ID = PREFIX + "sample-id";
	public static final String PROJECT_NAME = PREFIX + "project-name";
	public static final String PROJECT_ID = PREFIX + "project-id";
	public static final String CREATED_DATE = PREFIX + "created";
	public static final String MODIFIED_DATE = PREFIX + "modified";

	public UISampleMetadata(Project project, Sample sample) {
		this.put(SAMPLE_ID, String.valueOf(sample.getId()));
		this.put(SAMPLE_NAME, sample.getLabel());
		this.put(PROJECT_ID, String.valueOf(project.getId()));
		this.put(PROJECT_NAME, project.getLabel());
		this.put(CREATED_DATE, sample.getCreatedDate()
				.toString());
		this.put(MODIFIED_DATE, sample.getModifiedDate()
				.toString());
		this.putAll(getAllMetadataForSample(sample));
	}

	/**
	 * Convert the sample metadata into a format that can be consumed by Ag Grid.
	 *
	 * @param sample {@link Sample}
	 * @return {@link Map} of {@link String} field and {@link String} value
	 */
	private Map<String, String> getAllMetadataForSample(Sample sample) {
		Map<String, String> entries = new HashMap<>();
		Map<MetadataTemplateField, MetadataEntry> sampleMetadata = sample.getMetadata();
		for (MetadataTemplateField field : sampleMetadata.keySet()) {
			MetadataEntry entry = sampleMetadata.get(field);
			// Label must be converted into the proper format for client side look up purposes in Ag Grid.
			entries.put(PREFIX +field.getId(), entry.getValue());
		}
		return entries;
	}

}
