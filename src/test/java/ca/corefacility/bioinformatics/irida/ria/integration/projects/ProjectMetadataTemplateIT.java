package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectMetadataTemplatePage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSettingsMetadataTemplatesPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/ProjectMetadataTemplateView.xml")
public class ProjectMetadataTemplateIT extends AbstractIridaUIITChromeDriver {
	private final int PROJECT_ID = 1;

	@Test
	public void testCreateTemplateAsManager() {
		LoginPage.loginAsManager(driver());
		ProjectSettingsMetadataTemplatesPage page = ProjectSettingsMetadataTemplatesPage.goToPage(driver(), PROJECT_ID);
		assertEquals("Should be one template on the page", 1, page.getNumberOfTemplatesInProject());
	}

	@Test
	public void testCreatingNewTemplate() {
		LoginPage.loginAsManager(driver());

		String templateName = "TEMP NAME";
		ProjectMetadataTemplatePage page = ProjectMetadataTemplatePage.goToPage(driver(), PROJECT_ID);
		page.setTemplateName(templateName);
		assertFalse("Save button should not be enabled with only a template name.", page.isSaveButtonEnabled());

		page.addMetadataField("Test Field");
		assertEquals("Should be 1 metadata field on the page.", 1, page.getNumberOfTemplateFields());
	}
}
