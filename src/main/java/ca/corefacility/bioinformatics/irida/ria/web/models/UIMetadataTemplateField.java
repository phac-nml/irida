package ca.corefacility.bioinformatics.irida.ria.web.models;

import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;

public class UIMetadataTemplateField {
	private Long id;
	private String label;
	private boolean hide;

	public UIMetadataTemplateField() {
	}

	public UIMetadataTemplateField(MetadataTemplateField field, boolean hide) {
		this.id = field.getId();
		this.label = field.getLabel();
		this.hide = hide;
	}

	public Long getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public boolean isHide() {
		return hide;
	}
}
