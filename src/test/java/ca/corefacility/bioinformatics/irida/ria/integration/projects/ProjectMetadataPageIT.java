package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITPhantomJS;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectMetadataPage;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * <p>
 * Integration test to ensure that the Project Details Page.
 * </p>
 *
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class ProjectMetadataPageIT extends AbstractIridaUIITPhantomJS {
	private final String PAGE_TITLE = "IRIDA Platform - project2 - Metadata";
	private final Long PROJECT_ID_AS_OWNER = 2L;
	private final Long PROJECT_ID_AS_COLLABORATOR = 1L;
	private final String PROJECT_NAME = "project2";
	private final String PROJECT_DESCRIPTION = "This is another interesting project description.";
	private final String PROJECT_ORGANISM = "Salmonella";
	private final String PROJECT_REMOTE_URL = "http://salmonella-wiki.ca";

	private ProjectMetadataPage page;

	@Before
	public void setUpTest() {
		page = new ProjectMetadataPage(driver());
	}

	@Test
	public void displaysTheProjectMetaData() {
		LoginPage.loginAsUser(driver());
		page.goTo(PROJECT_ID_AS_OWNER);
		assertEquals("Displays the correct page title", PAGE_TITLE, driver().getTitle());
		assertEquals("Displays the correct project name", PROJECT_NAME, page.getDataProjectName());
		assertEquals("Displays the correct description", PROJECT_DESCRIPTION, page.getDataProjectDescription());
		assertEquals("Displays the correct organism", PROJECT_ORGANISM, page.getDataProjectOrganism());
		assertEquals("Displays the correct remoteURL", PROJECT_REMOTE_URL, page.getDataProjectRemoteURL());
		assertTrue("Contains edit metadata button", page.hasEditButton());

		// Should not have edit button on project that is not owner of.
		page.goTo(PROJECT_ID_AS_COLLABORATOR);
		assertFalse("Should not contain the edit medtadata button if they are only a collaborator",
				page.hasEditButton());
	}

}
