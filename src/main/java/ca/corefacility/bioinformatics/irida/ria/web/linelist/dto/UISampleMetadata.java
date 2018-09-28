package ca.corefacility.bioinformatics.irida.ria.web.linelist.dto;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.ria.web.components.agGrid.AgGridUtilities;

/**
 * Represents {@link Sample} metadata in the linelist table.
 */
public class UISampleMetadata {
	private Long sampleId;
	private String sample;
	private Long projectId;
	private String projectLabel;
	private Date createdDate;
	private Date modifiedDate;
	private Map<String, String> metadata;

	public UISampleMetadata(Project project, Sample sample) {
		this.sampleId = sample.getId();
		this.sample = sample.getLabel();
		this.projectId = project.getId();
		this.projectLabel = project.getLabel();
		this.createdDate = sample.getCreatedDate();
		this.modifiedDate = sample.getModifiedDate();
		this.metadata = getMetadataForSample(sample);
	}

	/**
	 * Convert the sample metadata into a format that can be consumed by Ag Grid.
	 *
	 * @param sample {@link Sample}
	 * @return {@link Map} of {@link String} field and {@link String} value
	 */
	private Map<String, String> getMetadataForSample(Sample sample) {
		Map<String, String> entries = new HashMap<>();
		Map<MetadataTemplateField, MetadataEntry> sampleMetadata = sample.getMetadata();
		for (MetadataTemplateField field : sampleMetadata.keySet()) {
			MetadataEntry entry = sampleMetadata.get(field);
			// Label must be converted into the proper format for client side look up purposes in Ag Grid.
			entries.put(AgGridUtilities.convertHeaderNameToField(field.getLabel()), entry.getValue());
		}
		return entries;
	}

	public Long getSampleId() {
		return sampleId;
	}

	public String getSample() {
		return sample;
	}

	public Long getProjectId() {
		return projectId;
	}

	public String getProjectLabel() {
		return projectLabel;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public Map<String, String> getMetadata() {
		return metadata;
	}
}
