package ca.corefacility.bioinformatics.irida.ria.unit.components;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.utilities.components.ProjectsDataTable;

/**
 * Unit test for {@link ProjectsDataTable}
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class ProjectsDataTableTest {

	@Test
	public void testGetSortStringFromColumnID() {
		assertEquals("Returns the correct sort string for a project id", "project.id",
				ProjectsDataTable.getSortStringFromColumnID(1));
		assertEquals("Returns the correct sort string for a project name", "project.name",
				ProjectsDataTable.getSortStringFromColumnID(2));
		assertEquals("Returns the correct sort string for a project organism", "project.organism",
				ProjectsDataTable.getSortStringFromColumnID(3));
		assertEquals("Returns the correct sort string for a project role", "projectRole",
				ProjectsDataTable.getSortStringFromColumnID(4));
		assertEquals("Returns the correct sort string for a project created date", "project.createdDate",
				ProjectsDataTable.getSortStringFromColumnID(7));
		assertEquals("Returns the correct sort string for a project modified date", "project.modifiedDate",
				ProjectsDataTable.getSortStringFromColumnID(8));

	}

}
