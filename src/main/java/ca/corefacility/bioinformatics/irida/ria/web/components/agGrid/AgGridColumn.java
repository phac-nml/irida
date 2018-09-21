package ca.corefacility.bioinformatics.irida.ria.web.components.agGrid;

/**
 * AgGrid column is used to represent a generic AgGrid Column
 *
 * @see <a href="https://www.ag-grid.com/javascript-grid-column-properties/">Column Properties</a>
 */
public abstract class AgGridColumn {
	/**
	 * The name to render in the column header.
	 */
	private String headerName;

	/**
	 * Which type of column to render (data, text, etc...)
	 * TODO: Convert this into an enum?
	 */
	private String type;

	/**
	 * Set to true to hide this column initially
	 */
	private boolean hide;

	/**
	 * Set to true to make column editable.
	 */
	private boolean editable;

	public AgGridColumn() {
	}

	/**
	 * Create a column header for a UI Ag Grid instance
	 *
	 * @param headerName {@link String} the text to display in the column header
	 * @param type       {@link String} the type of column (date, text)
	 * @param hide       {@link Boolean} whether the column is visible or not
	 * @param editable   {@link Boolean} whether the contents of the cells in the column are editable.
	 */
	public AgGridColumn(String headerName, String type, boolean hide, boolean editable) {
		this.headerName = headerName;
		this.type = type;
		this.hide = hide;
		this.editable = editable;
	}

	public String getField() {
		return AgGridUtilities.convertHeaderNameToField(headerName);
	}

	public String getHeaderName() {
		return headerName;
	}

	public String getType() {
		return type;
	}

	public boolean isHide() {
		return hide;
	}

	public boolean isEditable() {
		return editable;
	}
}
