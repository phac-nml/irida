package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.AssociatedProjectEditPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.RemoteApiUtilities;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.TestUtilities;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.Lists;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/AssociatedProjectsPageIT.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class AssociatedProjectsEditPageIT {
	private static final Logger logger = LoggerFactory.getLogger(AssociatedProjectsEditPageIT.class);

	AssociatedProjectEditPage page;

	private static final Long projectId = 1L;

	private WebDriver driver;
	private static final List<Long> ASSOCIATED_PROJECTS = Lists.newArrayList(2L, 3L, 5L);

	@Before
	public void setUp() {
		driver = TestUtilities.setDriverDefaults(new PhantomJSDriver());
		LoginPage.loginAsAdmin(driver);

		page = new AssociatedProjectEditPage(driver);
	}

	@After
	public void destroy() {
		if (driver != null) {
			driver.close();
			driver.quit();
		}
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
	public void hasInitialAssocaitedProjects() {
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
		page.checkNotyStatus("success");
		assertTrue("Project should be associated", isProjectAssociated(4L));
	}

	@Test
	public void testRemoveAssociatedProject() {
		page.goTo(projectId);
		logger.debug("Testing: testAddAssociatedProject");
		page.clickAssociatedButton(2L);
		page.checkNotyStatus("success");
		assertFalse("Project should not be associated", isProjectAssociated(2L));
	}

	@Test
	public void testAddRemoteAssociatedProject() {
		RemoteApiUtilities.addRemoteApi(driver);
		page.goTo(projectId);
		page.viewRemoteTab();
		page.clickAssociatedButton(4L);
		page.checkNotyStatus("success");
		assertTrue("Project should be associated", isProjectAssociated(4L));

	}

	private boolean isProjectAssociated(Long projectId) {
		List<String> projectsDiv = page.getAssociatedProjects();

		return projectsDiv.contains(projectId.toString());
	}
}
