package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectMetadataTemplatePage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSettingsMetadataTemplatesPage;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;

import static org.junit.Assert.*;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/ProjectMetadataTemplateView.xml")
public class ProjectMetadataTemplateIT extends AbstractIridaUIITChromeDriver {
	private final int PROJECT_ID = 1;
	private final int TEMPLATE_ID = 1;
	private final int NEW_TEMPLATE = -1;

	@Test
	public void testSettingsMetadataPage() {
		LoginPage.loginAsManager(driver());
		ProjectSettingsMetadataTemplatesPage page = ProjectSettingsMetadataTemplatesPage.goToPage(driver(), PROJECT_ID);
		assertEquals("Should be one template on the page", 1, page.getNumberOfTemplatesInProject());
	}

	@Test
	public void testCreatingNewTemplate() {
		LoginPage.loginAsManager(driver());

		String templateName = "TEMP NAME";
		ProjectMetadataTemplatePage page = ProjectMetadataTemplatePage.goToPage(driver(), PROJECT_ID, NEW_TEMPLATE);
		page.setTemplateName(templateName);
		assertFalse("Save button should not be enabled with only a template name.", page.isSaveButtonEnabled());

		page.addMetadataField("Test Field");
		assertEquals("Should be 1 metadata field on the page.", 1, page.getNumberOfTemplateFields());
		assertTrue("Save template button should be enabled if there is a template name and at least 1 field", page.isSaveButtonEnabled());

		page.saveTemplate();
		ProjectSettingsMetadataTemplatesPage settingsMetadataTemplatesPage = ProjectSettingsMetadataTemplatesPage.goToPage(driver(), PROJECT_ID);
		assertEquals("Should be two template on the Metadata Template List page", 2, settingsMetadataTemplatesPage.getNumberOfTemplatesInProject());
	}

	@Test
	public void testModifyExistingTemplate() {
		LoginPage.loginAsManager(driver());
		ProjectMetadataTemplatePage page = ProjectMetadataTemplatePage.goToPage(driver(), PROJECT_ID, TEMPLATE_ID);

		String currentTemplateName = page.getTemplateName();
		int currentNumTemplateFields = page.getNumberOfTemplateFields();

		// Remove  1 field
		page.removeTemplateFieldByIndex(1);
		assertEquals("Should be 1 less field", currentNumTemplateFields - 1, page.getNumberOfTemplateFields());

		// Change the template name
		String newTemplateName = currentTemplateName + "-new";
		page.setTemplateName(newTemplateName);
		assertFalse("Should have an updated template name", currentTemplateName.equalsIgnoreCase(page.getTemplateName()));

		page.saveTemplate();
		// Make sure the template was saved correctly
		driver().navigate().refresh();
		assertEquals("Should be 1 less field", currentNumTemplateFields - 1, page.getNumberOfTemplateFields());
		assertEquals("Template name should be updated", newTemplateName, page.getTemplateName());
	}
}
