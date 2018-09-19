package ca.corefacility.bioinformatics.irida.ria.web.linelist.dto;

/**
 * Headers and cell data in AgGrid reference each other using a common 'field'
 * This is to enforce the field is added correctly.
 */
public class AbstractTableItem {
	private String field;

	AbstractTableItem(String field) {
		this.field = stripLabelToField(field);
	}

	/**
	 * AgGrid uses the field to match the header with the correct column.  It
	 * appears to have issues special characters.
	 *
	 * @param field {@link String} the field name.
	 * @return {@link String} formatted field name.
	 */
	private String stripLabelToField(String field) {
		return field.replaceAll("[^a-zA-Z0-9]+", "-");
	}

	public String getField() {
		return field;
	}
}
