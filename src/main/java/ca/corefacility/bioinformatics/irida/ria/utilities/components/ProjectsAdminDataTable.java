package ca.corefacility.bioinformatics.irida.ria.utilities.components;

import com.google.common.collect.ImmutableMap;

/**
 * DataTables information for listing all projects for administrators.
 *
 */
public class ProjectsAdminDataTable extends DataTable {

	// Project Table Columns
	private static final int ID_COL = 1;
	private static final int NAME_COL = 2;
	private static final int ORGANISM_COL = 3;
	private static final int CREATED_DATE_COL = 7;
	private static final int MODIFIED_DATE_COL = 8;

	// Sort information
	private static final String SORT_BY_ID = "id";
	private static final String SORT_BY_NAME = "name";
	private static final String SORT_BY_ORGANISM = "organism";
	private static final String SORT_BY_CREATED_DATE = "createdDate";
	private static final String SORT_BY_MODIFIED_DATE = "modifiedDate";

	// Table default sort information
	public static final String SORT_DEFAULT_COLUMN = SORT_BY_MODIFIED_DATE + "";
	public static final String SORT_DEFAULT_DIRECTION = "desc";

	// Key is the column number in the datatable.
	private static final ImmutableMap<Integer, String> COLUMN_SORT_MAP = ImmutableMap.<Integer, String>builder()
			.put(ID_COL, SORT_BY_ID).put(NAME_COL, SORT_BY_NAME).put(ORGANISM_COL, SORT_BY_ORGANISM)
			.put(CREATED_DATE_COL, SORT_BY_CREATED_DATE)
			.put(MODIFIED_DATE_COL, SORT_BY_MODIFIED_DATE).build();

	/**
	 * Determines the property of the
	 * {@link ca.corefacility.bioinformatics.irida.model.Project} to sort by.
	 *
	 * @param id DataTables column id
	 * @return property of the
	 * {@link ca.corefacility.bioinformatics.irida.model.Project} to
	 * sort by.
	 */
	public static String getSortStringFromColumnID(int id) {
		return COLUMN_SORT_MAP.containsKey(id) ? COLUMN_SORT_MAP.get(id) : SORT_BY_MODIFIED_DATE;
	}
}
