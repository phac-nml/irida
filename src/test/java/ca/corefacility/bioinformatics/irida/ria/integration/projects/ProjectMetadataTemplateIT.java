package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSettingsMetadataTemplatesPage;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.Assert.assertEquals;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/ProjectMetadataTemplateView.xml")
public class ProjectMetadataTemplateIT extends AbstractIridaUIITChromeDriver {
	private final int PROJECT_ID = 1;

	// Test creating a template as user;
	@Test
	public void testCreateTemplateAsUser() {
		LoginPage.loginAsUser(driver());
		ProjectSettingsMetadataTemplatesPage page = ProjectSettingsMetadataTemplatesPage.goToPage(driver(), PROJECT_ID);
		assertEquals("Shoud be able to see metadata templates", 1, page.getNumberOfTemplatesInProject());
	}
}
