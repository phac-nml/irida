package ca.corefacility.bioinformatics.irida.ria.web.components.agGrid;

/**
 * AgGrid column is used to represent a generic AgGrid Column
 *
 * @see <a href="https://www.ag-grid.com/javascript-grid-column-properties/">Column Properties</a>
 */
public class AgGridColumn {
	/**
	 * The name to render in the column header.
	 */
	private String headerName;

	/**
	 * The field of the row to get the cells data from
	 */
	private String field;

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

	/**
	 * Set 'left' or 'right' to pin the that side of table
	 */
	private String pinned;

	/**
	 * Set to true to always have column displayed first.
	 */
	private boolean lockPosition;

	/**
	 * Set to true block pinning column via the UI
	 */
	private boolean lockPinned;

	/**
	 * Set to true to render a selection checkbox in the column.
	 */
	private boolean checkboxSelection;

	/**
	 * Set to true to render a select all /  none checkbox in the column header
	 */
	private boolean headerCheckboxSelection;

	/**
	 * Type of column filter.
	 */
	private String filter;

	/**
	 * Suppress the ability to resize this column
	 */
	private boolean resizable;

	/**
	 * Set to 'asc' or 'desc' to sort by this column by default.
	 */
	private String sort;

	public AgGridColumn() {
	}

	/**
	 * Create a column header for a UI Ag Grid instance
	 *
	 * @param headerName {@link String} the text to display in the column header
	 * @param field      {@link String} the key to the row data
	 * @param type       {@link String} the type of column (date, text)
	 * @param hide       {@link Boolean} whether the column is visible or not
	 * @param editable   {@link Boolean} whether the contents of the cells in the column are editable.
	 */
	public AgGridColumn(String headerName, String field, String type, boolean hide, boolean editable) {
		this.headerName = headerName;
		this.field = field;
		this.type = type;
		this.hide = hide;
		this.editable = editable;
		// Default to be resizable unless explicitly set.
		this.resizable = true;
	}

	public String getField() {
		return field;
	}

	public String getHeaderName() {
		return headerName;
	}

	public String getType() {
		return type;
	}

	public void setHide(boolean hide) {
		this.hide = hide;
	}

	public boolean isHide() {
		return hide;
	}

	public boolean isEditable() {
		return editable;
	}

	public String getPinned() {
		return pinned;
	}

	public void setPinned(String position) {
		this.pinned = position;
	}

	public boolean isLockPosition() {
		return lockPosition;
	}

	public void setLockPosition(boolean lockPosition) {
		this.lockPosition = lockPosition;
	}

	public boolean isLockPinned() {
		return lockPinned;
	}

	public void setLockPinned(boolean lockPinned) {
		this.lockPinned = lockPinned;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public boolean isCheckboxSelection() {
		return checkboxSelection;
	}

	public void setCheckboxSelection(boolean checkboxSelection) {
		this.checkboxSelection = checkboxSelection;
	}

	public boolean isHeaderCheckboxSelection() {
		return headerCheckboxSelection;
	}

	public void setHeaderCheckboxSelection(boolean headerCheckboxSelection) {
		this.headerCheckboxSelection = headerCheckboxSelection;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public boolean isResizable() {
		return resizable;
	}

	public void setResizable(boolean resizable) {
		this.resizable = resizable;
	}
}
