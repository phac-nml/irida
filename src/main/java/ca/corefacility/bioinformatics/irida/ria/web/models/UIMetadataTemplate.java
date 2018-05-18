package ca.corefacility.bioinformatics.irida.ria.web.models;

import java.util.ArrayList;
import java.util.Iterator;
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
	private List<UIMetadataTemplateField> fields;

	public UIMetadataTemplate() {
	}

	public UIMetadataTemplate(MetadataTemplate metadataTemplate) {
		this.id = metadataTemplate.getId();
		this.name = metadataTemplate.getName();
		this.fields = new ArrayList<>();
		for (MetadataTemplateField field : metadataTemplate.getFields()) {
			this.fields.add(new UIMetadataTemplateField(field, false));
		}
	}

	public UIMetadataTemplate(MetadataTemplate metadataTemplate, List<MetadataTemplateField> allFields) {
		this.id = metadataTemplate.getId();
		this.name = metadataTemplate.getName();
		List<MetadataTemplateField> templateFields = metadataTemplate.getFields();
		List<UIMetadataTemplateField> fields = new ArrayList<>();
		if (templateFields.isEmpty()) {
			// This would be for the default "all fields" template
			for (MetadataTemplateField field : allFields) {
				fields.add(new UIMetadataTemplateField(field, false));
			}
		} else {
			for (MetadataTemplateField field : templateFields) {
				// Need to remove legacy sampleNames that should not have been added.
				if (!field.getLabel()
						.equals("sampleName")) {
					fields.add(new UIMetadataTemplateField(field, false));
					allFields.remove(field);
				}
			}
			for (Iterator<MetadataTemplateField> fieldIT = allFields.iterator(); fieldIT.hasNext(); ) {
				MetadataTemplateField field = fieldIT.next();
				// Need to remove legacy sampleNames that should not have been added.
				if (!field.getLabel()
						.equals("sampleName")) {
					fields.add(new UIMetadataTemplateField(field, true));
					fieldIT.remove();
				}
			}
		}
		this.fields = fields;
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

	public List<UIMetadataTemplateField> getFields() {
		return fields;
	}

	public void setFields(List<UIMetadataTemplateField> fields) {
		this.fields = fields;
	}
}
