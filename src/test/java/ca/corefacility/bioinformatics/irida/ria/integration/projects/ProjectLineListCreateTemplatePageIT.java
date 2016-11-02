package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static junit.framework.TestCase.assertFalse;

import org.junit.Before;
import org.junit.Test;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectLineListCreateTemplatePage;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/ProjectLineListCreateTemplatePage.xml")
public class ProjectLineListCreateTemplatePageIT extends AbstractIridaUIITChromeDriver {
	@Before
	public void init() {
		LoginPage.loginAsManager(driver());
	}

	@Test
	public void testPageSetup() {
		ProjectLineListCreateTemplatePage page = ProjectLineListCreateTemplatePage.goToPage(driver());
		assertFalse("Save button should initially be disabled.", page.isSaveBtnEnabled());
	}
}
