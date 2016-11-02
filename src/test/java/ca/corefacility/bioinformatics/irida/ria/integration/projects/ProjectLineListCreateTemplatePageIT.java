package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertFalse;

import org.junit.Before;
import org.junit.Test;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectLineListCreateTemplatePage;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/ProjectLineListCreateTemplatePage.xml")
public class ProjectLineListCreateTemplatePageIT extends AbstractIridaUIITChromeDriver {
	public static final String SHORT_TEMPLATE_NAME = "aa";
	public static final String GOOD_TEMPLATE_NAME = "Unique Template Name";
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
		assertTrue("Save button should be enabled when a name longer than 3 characters is present",
				page.isSaveBtnEnabled());
	}
}
