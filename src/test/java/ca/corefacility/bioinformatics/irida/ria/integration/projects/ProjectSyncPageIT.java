package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectMetadataPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSyncPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.RemoteApiUtilities;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class ProjectSyncPageIT extends AbstractIridaUIITChromeDriver {

	ProjectSyncPage page;

	@Before
	public void setUpTest() {
		LoginPage.loginAsAdmin(driver());
		RemoteApiUtilities.addRemoteApi(driver());
		page = ProjectSyncPage.goTo(driver());
	}

	@Test
	public void testSyncProject() {
		page.selectApi(0);
		assertTrue("Projects should be shown in dropdown", page.areProjectsAvailable());
		page.selectProjectInListing(1);
		String selectedProjectName = page.getSelectedProjectName();

		page.openAdvanced();
		String url = page.getProjectUrl();
		assertFalse("URL should not be empty", url.isEmpty());
		page.submitProject();

		ProjectMetadataPage projectMetadataPage = new ProjectMetadataPage(driver());
		String dataProjectName = projectMetadataPage.getDataProjectName();

		assertEquals("Project names should be equal", selectedProjectName, dataProjectName);
	}
}
