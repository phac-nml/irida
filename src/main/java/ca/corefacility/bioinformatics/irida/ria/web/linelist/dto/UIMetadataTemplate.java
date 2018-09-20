package ca.corefacility.bioinformatics.irida.ria.web.linelist.dto;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.ria.web.linelist.dto.AbstractUIMetadataField;

/**
 * User interface model for a {@link MetadataTemplate}
 * This is required for creating a new {@link MetadataTemplate} since the UI create new {@link MetadataTemplateField}
 * only lists {@link String} representations of them.
 */
public class UIMetadataTemplate {
	private Long id;
	private String name;
	private List<AbstractUIMetadataField> fields;

	public UIMetadataTemplate() {
	}

	public UIMetadataTemplate(Long id, String name, List<AbstractUIMetadataField> fields) {
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

	public List<AbstractUIMetadataField> getFields() {
		return fields;
	}
}
