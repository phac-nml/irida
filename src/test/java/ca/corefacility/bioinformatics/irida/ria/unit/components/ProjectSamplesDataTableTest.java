package ca.corefacility.bioinformatics.irida.ria.unit.components;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.utilities.components.ProjectSamplesDataTable;

/**
 * Unit test for (
 */
public class ProjectSamplesDataTableTest {

	@Test
	public void testGetSortStringFromColumnID() {
		assertEquals("Returns expected sort string value for name column", "sample.sampleName",
				ProjectSamplesDataTable.getSortStringFromColumnID(2));
		assertEquals("Returns expected sort string value for created date column", "createdDate",
				ProjectSamplesDataTable.getSortStringFromColumnID(4));
	}
}
