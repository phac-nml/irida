package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.clients.ClientDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.clients.CreateClientPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSyncPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.RemoteApiUtilities;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.Assert.*;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class ProjectSyncPageIT extends AbstractIridaUIITChromeDriver {

	CreateClientPage createClientPage;
	ProjectSyncPage page;

	String clientId = "myClient";
	String clientSecret;

	@Test
	public void testSyncProject() {
		LoginPage.loginAsAdmin(driver());

		//create the oauth client
		String redirectLocation = RemoteApiUtilities.getRedirectLocation();
		createClientPage = new CreateClientPage(driver());
		createClientPage.goTo();
		createClientPage.createClientWithDetails(clientId, "authorization_code", redirectLocation, true, false);
		ClientDetailsPage detailsPage = new ClientDetailsPage(driver());
		clientSecret = detailsPage.getClientSecret();

		RemoteApiUtilities.addRemoteApi(driver(), clientId, clientSecret);
		page = ProjectSyncPage.goTo(driver());
		page.selectApi(0);
		assertTrue("Projects should be shown in dropdown", page.areProjectsAvailable());
		page.selectProjectInListing(1);
		String selectedProjectName = page.getSelectedProjectName();

		page.openAdvanced();
		String url = page.getProjectUrl();
		assertFalse("URL should not be empty", url.isEmpty());
		page.submitProject();

		ProjectDetailsPage projectDetailsPage = ProjectDetailsPage.initElements(driver());
		String dataProjectName = projectDetailsPage.getProjectName();

		assertEquals("Project names should be equal", selectedProjectName, dataProjectName);
	}
}
