package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.AssociatedProjectEditPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.Lists;

import static org.junit.Assert.*;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/AssociatedProjectsPageIT.xml")
public class AssociatedProjectsEditPageIT extends AbstractIridaUIITChromeDriver {
	private static final Logger logger = LoggerFactory.getLogger(AssociatedProjectsEditPageIT.class);

	AssociatedProjectEditPage page;

	private static final Long projectId = 1L;

	private static final List<Long> ASSOCIATED_PROJECTS = Lists.newArrayList(2L, 3L, 5L);

	@Before
	public void setUpTest() {
		page = new AssociatedProjectEditPage(driver());
		LoginPage.loginAsManager(driver());
	}

	@Test
	public void hasTheCorrectProjectsDisplayed() {
		page.goTo(projectId);
		logger.debug("Testing: hasTheCorrectProjectsDisplayed");
		List<String> projectsDiv = page.getProjects();
		assertEquals("Has the correct number of projects", 5, projectsDiv.size());

		assertFalse("Current project should not be displayed", projectsDiv.contains("1"));
	}

	@Test
	public void hasInitialAssociatedProjects() {
		page.goTo(projectId);
		logger.debug("Testing: hasTheCorrectProjectsDisplayed");
		List<String> projectsDiv = page.getAssociatedProjects();
		assertEquals("Has the correct number of associated projects", 3, projectsDiv.size());
		for (Long projectId : ASSOCIATED_PROJECTS) {
			assertTrue("Project " + projectId + " should be associated", projectsDiv.contains(projectId.toString()));
		}
	}

	@Test
	public void testAddAssociatedProject() {
		page.goTo(projectId);
		logger.debug("Testing: testAddAssociatedProject");
		page.clickAssociatedButton(4L);
		assertTrue("Should display success notification", page.checkSuccessNotification());
		assertTrue("Project should be associated", isProjectAssociated(4L));
	}

	@Test
	public void testRemoveAssociatedProject() {
		page.goTo(projectId);
		logger.debug("Testing: testAddAssociatedProject");
		page.clickAssociatedButton(2L);
		assertTrue("Should display success notification", page.checkSuccessNotification());
		assertFalse("Project should not be associated", isProjectAssociated(2L));
	}

	private boolean isProjectAssociated(Long projectId) {
		List<String> projectsDiv = page.getAssociatedProjects();

		return projectsDiv.contains(projectId.toString());
	}
}
