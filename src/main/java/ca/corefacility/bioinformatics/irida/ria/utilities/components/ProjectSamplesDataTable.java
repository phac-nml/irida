package ca.corefacility.bioinformatics.irida.ria.utilities.components;

import com.google.common.collect.ImmutableMap;

/**
 * Specific {@link DataTable} for Project > Samples
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class ProjectSamplesDataTable extends DataTable {
	public static final String ID = "id";
	public static final String CREATED_DATE = "createdDate";
	public static final String NUM_FILES = "numFiles";
	public static final String NAME = "name";
	// Table default sort information
	public static final String SORT_DEFAULT_COLUMN = CREATED_DATE;
	public static final String SORT_DEFAULT_DIRECTION = "desc";
	private static final int NAME_COL = 2;
	private static final int CREATED_DATE_COL = 4;
	private static final ImmutableMap<Integer, String> COLUMN_MAP_SORT = ImmutableMap.<Integer, String> builder()
			.put(NAME_COL, "sample.sampleName").put(CREATED_DATE_COL, CREATED_DATE).build();

	/**
	 * The the name of the table column based on the column id.
	 * 
	 * @param id
	 *            The column in the table
	 * @return String name of the column
	 */
	public static String getSortStringFromColumnID(int id) {
		return COLUMN_MAP_SORT.containsKey(id) ? COLUMN_MAP_SORT.get(id) : CREATED_DATE;
	}
}
