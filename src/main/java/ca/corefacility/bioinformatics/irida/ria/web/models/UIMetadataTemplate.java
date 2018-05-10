package ca.corefacility.bioinformatics.irida.ria.web.models;

import java.util.ArrayList;
import java.util.List;

import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;

/**
 * User interface model for a {@link MetadataTemplate}
 * This is required for creating a new {@link MetadataTemplate} since the UI create new {@link MetadataTemplateField}
 * only lists {@link String} representations of them.
 */
public class UIMetadataTemplate {
	private Long id;
	private String name;
	private List<MetadataTemplateField> fields;

	public UIMetadataTemplate() {
	}

	public UIMetadataTemplate(MetadataTemplate metadataTemplate) {
		this.id = metadataTemplate.getId();
		this.name = metadataTemplate.getName();
		this.fields = new ArrayList<>();
		this.fields.addAll(metadataTemplate.getFields());
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<MetadataTemplateField> getFields() {
		return fields;
	}

	public void setFields(List<MetadataTemplateField> fields) {
		this.fields = fields;
	}
}
