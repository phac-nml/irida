package ca.corefacility.bioinformatics.irida.ria.web.linelist.dao;

import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;

/**
 * UI representation of metadata template.  Designed to populate a dropdown menu
 * with the available templates.
 */
public class UIMetadataTemplate {
	private Long id;
	private String label;

	public UIMetadataTemplate(MetadataTemplate template) {
		this.id = template.getId();
		this.label = template.getLabel();
	}

	public Long getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}
}
