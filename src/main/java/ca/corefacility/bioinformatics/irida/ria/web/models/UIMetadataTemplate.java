package ca.corefacility.bioinformatics.irida.ria.web.models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
		this.fields = createDefaultFieldList(metadataTemplate.getFields());
	}

	public UIMetadataTemplate(MetadataTemplate template, List<MetadataTemplateField> allProjectFields) {
		this.id = template.getId();
		this.name = template.getName();
		this.fields = createTemplateFieldList(template, allProjectFields);
	}

	private List<UIMetadataTemplateField> createTemplateFieldList(MetadataTemplate template,
			List<MetadataTemplateField> allProjectFields) {
		List<MetadataTemplateField> fields = template.getFields();
		if (fields.isEmpty()) {
			// This would be for the default "all fields" template
			return createDefaultFieldList(fields);
		} else {
			return createTemplateFieldListFromTemplate(fields, allProjectFields);
		}
	}

	private List<UIMetadataTemplateField> createTemplateFieldListFromTemplate(
			List<MetadataTemplateField> templateFields, List<MetadataTemplateField> allProjectFields) {
		List<UIMetadataTemplateField> fields = new ArrayList<>();
		for (MetadataTemplateField field : templateFields) {
			// Need to remove legacy sampleNames that should not have been added.
			fields.add(new UIMetadataTemplateField(field, false));
			allProjectFields.remove(field);
		}

		fields.addAll(allProjectFields.stream()
				.map(field -> new UIMetadataTemplateField(field, true))
				.collect(Collectors.toList()));
		return fields;
	}

	private List<UIMetadataTemplateField> createDefaultFieldList(List<MetadataTemplateField> templateFields) {
		return templateFields.stream()
				.map(field -> new UIMetadataTemplateField(field, false))
				.collect(Collectors.toList());
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
