package ca.corefacility.bioinformatics.irida.ria.web.ajax.metadata.dto;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;

public class CreateMetadataTemplateRequest {
	private String name;
	private String description;
	private List<MetadataTemplateField> fields;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<MetadataTemplateField> getFields() {
		return fields;
	}

	public void setFields(List<MetadataTemplateField> fields) {
		this.fields = fields;
	}
}
