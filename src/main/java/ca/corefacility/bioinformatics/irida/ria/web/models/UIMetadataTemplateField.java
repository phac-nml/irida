package ca.corefacility.bioinformatics.irida.ria.web.models;

import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;

/**
 * Represents a {@link MetadataTemplateField} (column header) in the Line List table.
 */
public class UIMetadataTemplateField {
	private Long id;
	private String label;
	private String type; // TODO: This needs to be added to the MetadataTemplateField and removed from the Entry
	private boolean hide;

	public UIMetadataTemplateField() {
	}

	public UIMetadataTemplateField(MetadataTemplateField field, boolean hide) {
		this.id = field.getId();
		this.label = field.getLabel();
		this.type = "text";
		this.hide = hide;
	}

	public Long getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public String getType() {
		return type;
	}

	public boolean isHide() {
		return hide;
	}
}
