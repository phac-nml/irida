package ca.corefacility.bioinformatics.irida.ria.web.linelist.dto;

import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.ria.web.components.agGrid.AgGridColumn;

/**
 * This is a generic class to represent all possible headers (MetadataFields)
 * in a line list.
 */
public class UIMetadataField extends AgGridColumn {
	public UIMetadataField(MetadataTemplateField field, boolean hide, boolean editable) {
		super(field.getLabel(), field.getFieldKey(), field.getType(), hide, editable);
	}
}
