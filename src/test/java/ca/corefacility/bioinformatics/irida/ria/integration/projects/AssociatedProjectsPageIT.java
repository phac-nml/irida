package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITPhantomJS;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.AssociatedProjectPage;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class AssociatedProjectsPageIT extends AbstractIridaUIITPhantomJS {
	private static final Logger logger = LoggerFactory.getLogger(AssociatedProjectsPageIT.class);
	public static final ImmutableList<String> ASSOCIATED_PROJECTS_WITH_RIGHTS = ImmutableList
			.of("project2", "project3");

	private AssociatedProjectPage page;

	@Before
	public void setUpTest() {
		page = new AssociatedProjectPage(driver());
		LoginPage.loginAsManager(driver());
	}

	@Test
	public void hasTheCorrectAssociatedProjects() {
		logger.debug("Testing: hasTheCorrectAssociatedProjects");
		List<String> projectsDiv = page.getAssociatedProjects();
		assertEquals("Has the correct number of associated projects", 2, projectsDiv.size());

		List<String> projectsWithRights = page.getProjectsWithRights();
		for (String project : ASSOCIATED_PROJECTS_WITH_RIGHTS) {
			assertTrue("Contains projects with authorization (" + project + ")", projectsWithRights.contains(project));
		}
	}
}
