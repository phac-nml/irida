package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectLineListPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

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
	}

	@Test
	public void testTableSetup() {
		ProjectLineListPage page = ProjectLineListPage.goToPage(driver(), 1);
		assertEquals("Should be on the correct page.", "Line List", page.getActivePage());
		assertEquals("Should be 3 samples with metadata", 3, page.getNumberSamplesWithMetadata());

		// Make sure you can toggle table columns
		int initialCount = page.getNumberTableColumns();
		// Open the metadata column visible panel
		page.openColumnVisibilityPanel();
		// Toggle 2 columns
		page.toggleColumn("firstName");
		page.toggleColumn("healthAuthority");
		page.closeColumnVisibilityPanel();
		assertEquals("Should have 2 less columns visible", initialCount - 2, page.getNumberTableColumns());
	}

	@Test
	public void testTemplates() {
		ProjectLineListPage page = ProjectLineListPage.goToPage(driver(), 1);

		// Test selecting templates
		page.selectTemplate(TEMPLATE_1);
		assertEquals("Should have 4 columns visible", 3, page.getNumberTableColumns());

		// Test saving a template
		page.openColumnVisibilityPanel();
		page.toggleColumn("firstSymptom");
		page.closeColumnVisibilityPanel();
		page.saveTemplate(TEMPLATE_NAME);
		// Switch to a different template
		page.selectTemplate(TEMPLATE_1);
		assertEquals("Should have 4 columns visible", 3, page.getNumberTableColumns());
		page.selectTemplate(TEMPLATE_NAME);
		assertEquals("Should have 3 columns visible", 4, page.getNumberTableColumns());
	}
}
