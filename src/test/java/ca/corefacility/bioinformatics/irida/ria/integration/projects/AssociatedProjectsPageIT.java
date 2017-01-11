package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.ImmutableList;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.AssociatedProjectPage;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class AssociatedProjectsPageIT extends AbstractIridaUIITChromeDriver {
	private static final Logger logger = LoggerFactory.getLogger(AssociatedProjectsPageIT.class);
	public static final ImmutableList<String> ASSOCIATED_PROJECTS_WITH_RIGHTS = ImmutableList.of("project2",
			"project3");

	Long projectId = 1L;

	private AssociatedProjectPage page;

	@Before
	public void setUpTest() {
		page = new AssociatedProjectPage(driver());
	}

	@Test
	public void hasTheCorrectAssociatedProjects() {
		LoginPage.loginAsManager(driver());
		page.getPage(projectId);
		logger.debug("Testing: hasTheCorrectAssociatedProjects");
		List<String> projectsDiv = page.getAssociatedProjects();
		assertEquals("Has the correct number of associated projects", 2, projectsDiv.size());

		List<String> projectsWithRights = page.getProjectsWithRights();
		for (String project : ASSOCIATED_PROJECTS_WITH_RIGHTS) {
			assertTrue("Contains projects with authorization (" + project + ")", projectsWithRights.contains(project));
		}
	}
}
