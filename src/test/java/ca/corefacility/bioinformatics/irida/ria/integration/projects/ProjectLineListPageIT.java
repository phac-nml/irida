package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Dimension;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectLineListPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.Assert.assertEquals;

/**
 * <p>
 * Integration test to ensure that the Project Line List Page is working.
 * </p>
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/ProjectLineListView.xml")
public class ProjectLineListPageIT extends AbstractIridaUIITChromeDriver {
	private final String TEMPLATE_1 = "Testing Template 1";
	private final String TEMPLATE_NAME = "TESTER";

	@Before
	public void init() {
		LoginPage.loginAsManager(driver());
		driver().manage()
				.window()
				.setSize(new Dimension(1800, 900)); // Make sure we can see everything.
	}

	@Test
	public void testTableSetup() {
		ProjectLineListPage page = ProjectLineListPage.goToPage(driver(), 1);
		// OPen the column panel
		page.openColumnsPaenl();
		assertEquals("Should be on the correct page.", "Line List", page.getActivePage());
		assertEquals("Should be 21 samples", 21, page.getNumberOfRowsInLineList());
		assertEquals("Should be 8 fields to toggle", 8, page.getNumberOfMetadataFields());

		// There will be an extra header because you cannot toggle the sample column.
		assertEquals("Should be 8 table headers", 8, page.getNumberOfTableColumnsVisible());

		// Toggle one of the fields and make sure the table updates;
		page.toggleMetadataField(1);
		assertEquals("Should now only display 7 fields", 7, page.getNumberOfTableColumnsVisible());
		page.toggleMetadataField(2);
		assertEquals("Should now only display 6 fields", 6, page.getNumberOfTableColumnsVisible());

		// Test selecting templates
		page.selectTemplate(TEMPLATE_1);
		assertEquals("Should be 4 fields visible including the sample name", 4, page.getNumberOfTableColumnsVisible());

		// Test saving a template
		page.toggleMetadataField(1);

		assertEquals("Should have 3 columns visible", 3, page.getNumberOfTableColumnsVisible());
		page.saveMetadataTemplate(TEMPLATE_NAME);

		// Switch to a different template
		page.selectTemplate(TEMPLATE_1);
		assertEquals("Should have 4 columns visible", 4, page.getNumberOfTableColumnsVisible());

		// Switch back to new template
		page.selectTemplate(TEMPLATE_NAME);
		assertEquals("Should have 8 columns visible", 8, page.getNumberOfTableColumnsVisible());

		// Test inline editing
		String cellContents = page.getCellContents(0, "symptom");
		assertEquals("Sneezing", cellContents);

		String newValue = "FOOBAR";
		page.editCellContents(0, "symptom", newValue);
		assertEquals("Cell should contain the new edited value", newValue, page.getCellContents(0, "symptom"));

		driver().navigate()
				.refresh();
		page.openColumnsPaenl();
		page.selectTemplate(TEMPLATE_NAME);
		assertEquals("Should keep value on a page refresh", newValue, page.getCellContents(0, "symptom"));

		// Let's test to make sure that the undo works.
		String testValue = "THIS SHOULD BE GONE!";
		page.editCellContents(0, "symptom", testValue);
		page.cancelCellEdit();
		assertEquals("Should keep value after undoing edit", newValue, page.getCellContents(0, "symptom"));

		// Test table filtering
		page.filterTable(newValue);
		assertEquals("Should be only one row with the new value", 1, page.getNumberOfRowsInLineList());
		page.clearTableFilter();
		assertEquals("Should be 21 samples", 21, page.getNumberOfRowsInLineList());
	}
}
