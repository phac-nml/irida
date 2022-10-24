package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectLineListPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ShareSamplesPage;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 * Integration test to ensure that the Project Line List Page is working.
 * </p>
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/ProjectLineListView.xml")
public class ProjectLineListPageIT extends AbstractIridaUIITChromeDriver {
	private final String TEMPLATE_1 = "Testing Template 1";
	private final String TEMPLATE_NAME = "TESTER";
	private final String COLUMN_ID = "irida-4"; // This is is based on the id of the MetadataField.

	@Test
	public void testPageAsCollaborator() {
		LoginPage.loginAsUser(driver());
		driver().manage()
				.window()
				.setSize(new Dimension(1800, 1200)); // Make sure we can see everything.
		ProjectLineListPage page = ProjectLineListPage.goToPage(driver(), 1);
		assertFalse(page.isImportMetadataBtnVisible(), "Should not display import metadata button to collaborators");

		String newValue = "FOOBAR";
		page.editCellContents(0, COLUMN_ID, newValue);
		assertNotEquals(newValue, page.getCellContents(0, COLUMN_ID), "Cell should not have been updated");

		assertFalse(page.isShareButtonVisible(), "Collaborators should not be able to share samples");
	}

	@Test
	public void testPageAsManager() {
		LoginPage.loginAsManager(driver());
		driver().manage()
				.window()
				.setSize(new Dimension(1800, 1200)); // Make sure we can see everything.

		ProjectLineListPage page = ProjectLineListPage.goToPage(driver(), 1);
		assertTrue(page.isImportMetadataBtnVisible(), "Should display import metadata button");

		// Ensure translations are loaded onto the page.
		assertTrue(page.ensureTranslationsLoaded("app"), "Applications translations should be loaded");
		assertTrue(page.ensureTranslationsLoaded("project-linelist"),
				"Translations should be properly loaded on the linelist page.");


		// Test the tour to make sure everything is functional.
		page.openTour();
		assertEquals(1, page.getTourStep(), "Should be on the first step of the tour");
		page.goToNextTourStage();
		assertEquals(2, page.getTourStep(), "Should be on the second step of the tour");
		page.closeTour();
		// If we reached this far the tour is good to go

		// OPen the column panel
		page.openColumnsPanel();
		assertEquals("Line List", page.getActivePage(), "Should be on the correct page.");
		assertEquals(21, page.getNumberOfRowsInLineList(), "Should be 21 samples");
		assertEquals(6, page.getNumberOfMetadataFields(), "Should be 6 fields to toggle");

		// Toggle one of the fields and make sure the table updates;
		page.toggleMetadataField(1);
		assertEquals(6, page.getNumberOfTableColumnsVisible(), "Should now only display 6 fields");
		page.toggleMetadataField(2);
		assertEquals(5, page.getNumberOfTableColumnsVisible(), "Should now only display 5 fields");

		// Test selecting templates
		page.selectTemplate(TEMPLATE_1);
		assertEquals(4, page.getNumberOfTableColumnsVisible(), "Should be 4 fields visible including the sample name");

		// Test saving a template
		page.toggleMetadataField(1);

		assertEquals(3, page.getNumberOfTableColumnsVisible(), "Should have 3 columns visible");
		page.saveMetadataTemplate(TEMPLATE_NAME);

		// Switch to a different template
		page.selectTemplate(TEMPLATE_1);
		assertEquals(4, page.getNumberOfTableColumnsVisible(), "Should have 4 columns visible");

		// Switch back to new template
		page.selectTemplate(TEMPLATE_NAME);
		assertEquals(3, page.getNumberOfTableColumnsVisible(), "Should have 3 columns visible");

		// Test creating a second template
		page.toggleMetadataField(5);
		assertEquals(4, page.getNumberOfTableColumnsVisible(), "Should have 4 columns visible");
		page.saveMetadataTemplate("ANOTHER TESTING TEMPLATE");

		// Switch back to new template
		page.selectTemplate(TEMPLATE_NAME);
		assertEquals(3, page.getNumberOfTableColumnsVisible(), "Should have 3 columns visible");

		// Switch back to new template
		page.selectTemplate("ANOTHER TESTING TEMPLATE");
		assertEquals(4, page.getNumberOfTableColumnsVisible(), "Should have 4 columns visible");

		// Test inline editing
		page.selectTemplate("All Fields");
		String cellContents = page.getCellContents(0, COLUMN_ID);
		assertEquals("", cellContents);

		String newValue = "FOOBAR";
		page.editCellContents(0, COLUMN_ID, newValue);
		assertEquals(newValue, page.getCellContents(0, COLUMN_ID), "Cell should contain the new edited value");

		driver().navigate()
				.refresh();
		page.openColumnsPanel();
		page.selectTemplate(TEMPLATE_NAME);
		assertEquals(newValue, page.getCellContents(0, COLUMN_ID), "Should keep value on a page refresh");

		// Let's test to make sure that the undo works.
		String testValue = "THIS SHOULD BE GONE!";
		page.editCellContents(0, COLUMN_ID, testValue);
		page.cancelCellEdit();
		assertEquals(newValue, page.getCellContents(0, COLUMN_ID), "Should keep value after undoing edit");

		// Test table filtering
		page.filterTable(newValue);
		assertEquals(1, page.getNumberOfRowsInLineList(), "Should be only one row with the new value");
		page.clearTableFilter();
		assertEquals(21, page.getNumberOfRowsInLineList(), "Should be 21 samples");

		// Test sharing
		assertFalse(page.isShareButtonEnabled(), "Share button should not be enabled with no samples selected");
		page.selectRow(0);
		page.selectRow(1);
		page.selectRow(2);
		assertTrue(page.isShareButtonEnabled(), "Share button should now be enabled");
		page.shareSelectedSamples();
		assertTrue(driver().getCurrentUrl().contains("share"), "Should be on the share samples page");
		ShareSamplesPage shareSamplesPage = ShareSamplesPage.initPage(driver());
		shareSamplesPage.searchForProject("project2");
		shareSamplesPage.gotToNextStep();
		assertEquals(3, shareSamplesPage.getNumberOfSamplesDisplayed());
	}
}
