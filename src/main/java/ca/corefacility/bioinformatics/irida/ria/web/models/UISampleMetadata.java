package ca.corefacility.bioinformatics.irida.ria.web.models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.ria.web.linelist.dto.UIMetadataEntry;

/**
 * Represents {@link Sample} metadata in the linelist table.
 */
public class UISampleMetadata {
	private Long sampleId;
	private String sampleLabel;
	private Long projectId;
	private String projectLabel;
	private Date created;
	private Date modified;
	private Map<String, String> metadata;

	public UISampleMetadata(Project project, Sample sample) {
		this.sampleId = sample.getId();
		this.sampleLabel = sample.getLabel();
		this.projectId = project.getId();
		this.projectLabel = project.getLabel();
		this.created = sample.getCreatedDate();
		this.modified = sample.getModifiedDate();
		this.metadata = getMetadataForSample(sample);
	}

	private Map<String, String> getMetadataForSample(Sample sample) {
		Map<String, String> entries = new HashMap<>();
		Map<MetadataTemplateField, MetadataEntry> sampleMetadata = sample.getMetadata();
		for (MetadataTemplateField field : sampleMetadata.keySet()) {
			UIMetadataEntry entry = new UIMetadataEntry(field,
					sampleMetadata.getOrDefault(field, new MetadataEntry("", "text"))) ;
			entries.put(entry.getField(), entry.getValue());
		}
		return entries;
	}

	public Long getSampleId() {
		return sampleId;
	}

	public String getSampleLabel() {
		return sampleLabel;
	}

	public Long getProjectId() {
		return projectId;
	}

	public String getProjectLabel() {
		return projectLabel;
	}

	public Date getCreated() {
		return created;
	}

	public Date getModified() {
		return modified;
	}

	public Map<String, String> getMetadata() {
		return metadata;
	}
}
