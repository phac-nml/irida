package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSettingsMetadataTemplatesPage;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.Assert.*;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/ProjectMetadataTemplateView.xml")
public class ProjectMetadataTemplateIT extends AbstractIridaUIITChromeDriver {
	private final int PROJECT_ID = 1;

	@Test
	public void testCreateTemplateAsManager() {
		LoginPage.loginAsManager(driver());
		ProjectSettingsMetadataTemplatesPage page = ProjectSettingsMetadataTemplatesPage.goToPage(driver(), PROJECT_ID);
		assertEquals("Should be one template on the page", 1, page.getNumberOfTemplatesInProject());
	}
}
