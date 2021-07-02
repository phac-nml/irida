package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

import java.util.List;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.metadata.dto.ProjectMetadataField;

public class CopySamplesRequest {
	private Long original;
	private Long destination;
	private List<Long> sampleIds;
	private boolean owner;
	private List<ProjectMetadataField> fields;

	public Long getOriginal() {
		return original;
	}

	public void setOriginal(Long original) {
		this.original = original;
	}

	public Long getDestination() {
		return destination;
	}

	public void setDestination(Long destination) {
		this.destination = destination;
	}

	public List<Long> getSampleIds() {
		return sampleIds;
	}

	public void setSampleIds(List<Long> sampleIds) {
		this.sampleIds = sampleIds;
	}

	public boolean isOwner() {
		return owner;
	}

	public void setOwner(boolean owner) {
		this.owner = owner;
	}

	public List<ProjectMetadataField> getFields() {
		return fields;
	}

	public void setFields(List<ProjectMetadataField> fields) {
		this.fields = fields;
	}
}
