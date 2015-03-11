package ca.corefacility.bioinformatics.irida.ria.utilities.components;

import ca.corefacility.bioinformatics.irida.model.project.Project;

import com.google.common.collect.ImmutableMap;

/**
 * DataTables information for listing projects.
 *
 */
public class ProjectsDataTable extends DataTable {

	// Project Table Columns
	private static final int ID_COL = 1;
	private static final int NAME_COL = 2;
	private static final int ORGANISM_COL = 3;
	private static final int ROLE_COL = 4;
	private static final int CREATED_DATE_COL = 7;
	private static final int MODIFIED_DATE_COL = 8;

	// Sort information
	private static final String SORT_BY_ID = "project.id";
	private static final String SORT_BY_NAME = "project.name";
	private static final String SORT_BY_ROLE = "projectRole";
	private static final String SORT_BY_ORGANISM = "project.organism";
	private static final String SORT_BY_CREATED_DATE = "project.createdDate";
	private static final String SORT_BY_MODIFIED_DATE = "project.modifiedDate";

	// Table default sort information
	public static final String SORT_DEFAULT_COLUMN = SORT_BY_MODIFIED_DATE + "";
	public static final String SORT_DEFAULT_DIRECTION = "desc";

	// Key is the column number in the datatable.
	private static final ImmutableMap<Integer, String> COLUMN_SORT_MAP = ImmutableMap.<Integer, String> builder()
			.put(ID_COL, SORT_BY_ID).put(NAME_COL, SORT_BY_NAME).put(ORGANISM_COL, SORT_BY_ORGANISM)
			.put(ROLE_COL, SORT_BY_ROLE).put(CREATED_DATE_COL, SORT_BY_CREATED_DATE)
			.put(MODIFIED_DATE_COL, SORT_BY_MODIFIED_DATE).build();

	/**
	 * Determines the property of the {@link Project} to sort by.
	 * 
	 * @param id
	 *            DataTables column id
	 * @return property of the {@link Project} to sort by.
	 */
	public static String getSortStringFromColumnID(int id) {
		return COLUMN_SORT_MAP.containsKey(id) ? COLUMN_SORT_MAP.get(id) : SORT_BY_MODIFIED_DATE;
	}
}
