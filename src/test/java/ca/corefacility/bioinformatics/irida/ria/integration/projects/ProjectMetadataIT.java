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

		// TEMPLATES
		page.gotoMetadataTemplates();
		int numberOfMetadataTemplates = page.getNumberOfMetadataTemplates();
		Assert.assertEquals("Expect to display all metadata templates in the project", 1, numberOfMetadataTemplates);

		// Test field selection & template creation
		page.gotoMetadataFields();
		page.selectMetadataField("Province");
		page.selectMetadataField("Symptoms");
		page.selectMetadataField("Exposures");
		page.createNewTemplate("Special Template", "Long description");
		Assert.assertTrue("Should be on a template specific page", driver().getCurrentUrl().matches("(.*)/templates/\\d+"));
		page.gotoMetadataTemplates();

		Assert.assertEquals("Should be one more template than there was initially", numberOfMetadataTemplates + 1,
				page.getNumberOfMetadataTemplates());

		page.deleteTemplate("Test Template");
		Assert.assertEquals("Should be the same number of template as there was initially", numberOfMetadataTemplates,
				page.getNumberOfMetadataTemplates());
		String foobbar = "BAZ";
	}
}
