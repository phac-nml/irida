package ca.corefacility.bioinformatics.irida.ria.unit.components;

import ca.corefacility.bioinformatics.irida.ria.utilities.components.ProjectSamplesDataTable;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for (
 */
public class ProjectSamplesDataTableTest {

	@Test
	public void testGetSortStringFromColumnID() {
		assertEquals("Returns expected sort string value for name column", "sample.sampleName",
				ProjectSamplesDataTable.getSortStringFromColumnID(1));
		assertEquals("Returns expected sort string value for created date column", "createdDate",
				ProjectSamplesDataTable.getSortStringFromColumnID(3));
	}
}
