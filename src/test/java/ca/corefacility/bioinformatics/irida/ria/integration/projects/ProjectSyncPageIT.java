package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSyncPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class ProjectSyncPageIT extends AbstractIridaUIITChromeDriver {

	ProjectSyncPage page;

	String clientId = "myClient";
	String clientSecret;

	@Test
	public void testSyncProject() {
//		LoginPage.loginAsAdmin(driver());
//
//		//create the oauth client
//		String redirectLocation = RemoteApiUtilities.getRedirectLocation();
//		createClientPage = new CreateClientPage(driver());
//		createClientPage.goTo();
//		createClientPage.createClientWithDetails(clientId, "authorization_code", redirectLocation, true, false);
//		ClientDetailsPage detailsPage = new ClientDetailsPage(driver());
//		clientSecret = detailsPage.getClientSecret();
//
//		RemoteApiUtilities.addRemoteApi(driver(), clientId, clientSecret);
//		page = ProjectSyncPage.goTo(driver());
//		page.selectApi(0);
//		final String name = "project";
//		page.selectProjectInListing(name);
//
//		String url = page.getProjectUrl();
//		assertFalse("URL should not be empty", url.isEmpty());
//		page.submitProject();
//
//		ProjectDetailsPage projectDetailsPage = ProjectDetailsPage.initElements(driver());
//		String dataProjectName = projectDetailsPage.getProjectName();
//		assertEquals("Should be on the remote project page", dataProjectName, name);
	}
}
