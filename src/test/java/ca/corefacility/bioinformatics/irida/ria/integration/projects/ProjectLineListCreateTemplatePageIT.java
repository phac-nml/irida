package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectLineListCreateTemplatePage;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/ProjectLineListCreateTemplatePage.xml")
public class ProjectLineListCreateTemplatePageIT extends AbstractIridaUIITChromeDriver {
	private static final String SHORT_TEMPLATE_NAME = "aa";
	private static final String GOOD_TEMPLATE_NAME = "Unique Template Name";
	@Before
	public void init() {
		LoginPage.loginAsManager(driver());
	}

	@Test
	public void testPage() {
		ProjectLineListCreateTemplatePage page = ProjectLineListCreateTemplatePage.goToPage(driver());
		assertFalse("Save button should initially be disabled.", page.isSaveBtnEnabled());

		page.setNewTemplateName(SHORT_TEMPLATE_NAME);
		assertFalse("Save button should still be disabled", page.isSaveBtnEnabled());

		page.setNewTemplateName(GOOD_TEMPLATE_NAME);
		assertFalse("Save button should be disabled when a name longer than 5 characters is present, but no valid input",
				page.isSaveBtnEnabled());

		page.setFieldLabel(0, "firstLabel");
		assertTrue("Save button should be enabled when a name longer than 5 characters is present, and the fiel dis valid",
				page.isSaveBtnEnabled());
		page.addNewField();
		page.setFieldLabel(0, "secondLabel");

		assertEquals("There should be two fields on the page", 2, page.getNumberOfFields());

		page.removeField(1);

		assertEquals("There should only be one field left after removal", 1, page.getNumberOfFields());

		page.saveNewTemplate(GOOD_TEMPLATE_NAME);
		assertEquals("Should be redirected to the line list page", "IRIDA Platform - Line List", driver().getTitle());
	}
}
