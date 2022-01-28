package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectMetadataPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/ProjectMetadataView.xml")
public class ProjectMetadataIT extends AbstractIridaUIITChromeDriver {

	@Test
	public void testAdminProjectMetadata() {
		LoginPage.loginAsManager(driver());
		ProjectMetadataPage page = ProjectMetadataPage.goTo(driver());

		// FIELDS
		assertEquals(5, page.getNumberOfMetadataFields(),
				"Expected to display all metadata fields in the project");

		// TEST FIELD RESTRICTIONS
		assertTrue(page.areFieldRestrictionSettingsVisible(),
				"Fields restrictions settings should be visible to managers");
		assertEquals("Level 1", page.getFieldRestrictionForRow(0),
				"Should currently be set to level 1 by default");
		page.updateFieldRestrictionToLevel(0, 3);
		assertEquals("Level 4", page.getFieldRestrictionForRow(0), "Field should now be restricted to level 4");

		// TEMPLATES
		page.gotoMetadataTemplates();

		/*
		Check that the All Fields Template is the default template for the project on load as no default
		template has been set yet for the project
		 */
		assertTrue(page.allFieldsTemplateIsDefault());

		int numberOfMetadataTemplates = page.getNumberOfMetadataTemplates();
		// The All Fields template which is dynamically displayed + the one in the db
		assertEquals(2, numberOfMetadataTemplates, "Expect to display all metadata templates in the project");

		// Test field selection & template creation
		page.gotoMetadataFields();
		page.selectMetadataField("Province");
		page.selectMetadataField("Symptoms");
		page.selectMetadataField("Exposures");
		page.createNewTemplate("Special Template", "Long description");
		assertTrue(driver().getCurrentUrl().matches("(.*)/metadata/templates/\\d+"), "Should be on a template specific page");
		final String newTemplateName = "An awesome name";
		page.editTemplateName(newTemplateName);
		assertEquals(newTemplateName, page.getTemplateName(), "New template name should be set as the template name");

		page.gotoMetadataTemplates();

		assertEquals(numberOfMetadataTemplates + 1, page.getNumberOfMetadataTemplates(),
				"Should be one more template than there was initially");

		// Set the first template as the default for the project which is the template created above
		page.setDefaultTemplate(newTemplateName);
		// The remove button for the default template should be disabled.
		page.removeButtonIsDisabled();

		// The all fields template shouldn't be the default as we set the new template created above as the default
		assertFalse(page.allFieldsTemplateIsDefault());

		// The current default template
		page.gotoTemplate(newTemplateName);
		assertTrue(page.defaultTemplateTagVisible());

		page.gotoMetadataTemplates();

		// The other previous template which is not the default
		page.gotoTemplate("Test Template");
		// Since it's not a default template it should have the Set as Default button visible
		assertTrue(page.setDefaultTemplateButtonVisible());

		page.gotoMetadataTemplates();

		page.deleteTemplate("Test Template");
		assertEquals(numberOfMetadataTemplates, page.getNumberOfMetadataTemplates(),
				"Should be the same number of template as there was initially");

	}

	@Test
	public void testMemberProjectMetadata() {
		LoginPage.loginAsUser(driver());
		ProjectMetadataPage page = ProjectMetadataPage.goTo(driver());

		// TEST FIELD RESTRICTIONS
		assertFalse(page.areFieldRestrictionSettingsVisible(), "Fields restrictions settings should not be visible to collaborators");

		assertFalse(page.isCreateTemplateButtonVisible(), "Should not have a create template button");
		assertEquals(5, page.getNumberOfMetadataFields(), "Should be able to see the metadata fields");
		page.gotoMetadataTemplates();
		assertEquals(2, page.getNumberOfMetadataTemplates(), "Should be able to see the metadata templates");
		assertFalse(page.canDeleteTemplate(), "Should not be able to delete a template");

		page.gotoTemplate("test template");
		assertFalse(page.canEditTemplateName(), "Should not be able to edit the template name");
	}
}
