package ca.corefacility.bioinformatics.irida.ria.web.models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;

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

	public UIMetadataTemplate(Long id, String name, List<UIMetadataTemplateField> fields) {
		this.id = id;
		this.name = name;
		this.fields = fields;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<UIMetadataTemplateField> getFields() {
		return fields;
	}
}
