package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

import java.util.List;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.dto.MetadataFieldModel;

/**
 * UI Request to create a new sample
 */
public class CreateSampleRequest {
	private String name;
	private String organism;
	private String description;
	private List<MetadataFieldModel> metadata;

	public CreateSampleRequest() {
	}

	public CreateSampleRequest(String name, String organism) {
		this.name = name;
		this.organism = organism;
	}

	public CreateSampleRequest(String name, String organism, String description, List<MetadataFieldModel> metadata) {
		this.name = name;
		this.organism = organism;
		this.description = description;
		this.metadata = metadata;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOrganism(String organism) {
		this.organism = organism;
	}

	public String getName() {
		return name;
	}

	public String getOrganism() {
		return organism;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<MetadataFieldModel> getMetadata() {
		return metadata;
	}

	public void setMetadata(List<MetadataFieldModel> metadata) {
		this.metadata = metadata;
	}
}
