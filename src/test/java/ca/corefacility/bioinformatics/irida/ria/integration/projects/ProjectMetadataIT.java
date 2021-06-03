package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.Assert;
import org.junit.Test;

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
		Assert.assertEquals("Expected to display all metadata fields in the project", 5,
				page.getNumberOfMetadataFields());

		// TEST FIELD RESTRICTIONS
		Assert.assertTrue("Fields restrictions settings should be visible to managers", page.areFieldRestrictionSettingsVisible());
		Assert.assertEquals("Should currently be set to collaborator by default", "Collaborator", page.getFieldRestrictionForRow(0));
		page.updateFieldRestrictionToOwner(0);
		Assert.assertEquals("Field should now be restricted to managers", "Manager", page.getFieldRestrictionForRow(0));

		// TEMPLATES
		page.gotoMetadataTemplates();

		/*
		Check that the All Fields Template is the default template for the project on load as no default
		template has been set yet for the project
		 */
		Assert.assertTrue(page.allFieldsTemplateIsDefault());

		int numberOfMetadataTemplates = page.getNumberOfMetadataTemplates();
		// The All Fields template which is dynamically displayed + the one in the db
		Assert.assertEquals("Expect to display all metadata templates in the project", 2, numberOfMetadataTemplates);

		// Test field selection & template creation
		page.gotoMetadataFields();
		page.selectMetadataField("Province");
		page.selectMetadataField("Symptoms");
		page.selectMetadataField("Exposures");
		page.createNewTemplate("Special Template", "Long description");
		Assert.assertTrue("Should be on a template specific page", driver().getCurrentUrl().matches("(.*)/metadata/templates/\\d+"));
		final String newTemplateName = "An awesome name";
		final String currentName = page.getTemplateName();
		page.editTemplateName(newTemplateName);
		Assert.assertEquals("New template name should be set as the template name",newTemplateName, page.getTemplateName());

		page.gotoMetadataTemplates();

		Assert.assertEquals("Should be one more template than there was initially", numberOfMetadataTemplates + 1,
				page.getNumberOfMetadataTemplates());

		// Set the first template as the default for the project which is the template created above
		page.setDefaultTemplate();
		// The remove button for the default template should be disabled.
		page.removeButtonIsDisabled();

		// The all fields template shouldn't be the default as we set the new template created above as the default
		Assert.assertFalse(page.allFieldsTemplateIsDefault());

		// The current default template
		page.gotoTemplate("An awesome name");
		Assert.assertTrue(page.defaultTemplateTagVisible());

		page.gotoMetadataTemplates();

		// The other previous template which is not the default
		page.gotoTemplate("Test Template");
		// Since it's not a default template it should have the Set as Default button visible
		Assert.assertTrue(page.setDefaultTemplateButtonVisible());

		page.gotoMetadataTemplates();

		page.deleteTemplate("Test Template");
		Assert.assertEquals("Should be the same number of template as there was initially", numberOfMetadataTemplates,
				page.getNumberOfMetadataTemplates());

	}

	@Test
	public void testMemberProjectMetadata() {
		LoginPage.loginAsUser(driver());
		ProjectMetadataPage page = ProjectMetadataPage.goTo(driver());

		// TEST FIELD RESTRICTIONS
		Assert.assertFalse("Fields restrictions settings should not be visible to collaborators", page.areFieldRestrictionSettingsVisible());

		Assert.assertFalse("Should not have a create template button", page.isCreateTemplateButtonVisible());
		Assert.assertEquals("Should be able to see the metadata fields", 5, page.getNumberOfMetadataFields());
		page.gotoMetadataTemplates();
		Assert.assertEquals("Should be able to see the metadata templates", 2, page.getNumberOfMetadataTemplates());
		Assert.assertFalse("Should not be able to delete a template", page.canDeleteTemplate());

		page.gotoTemplate("test template");
		Assert.assertFalse("Should not be able to edit the template name", page.canEditTemplateName());
	}
}
