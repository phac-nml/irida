package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.Dimension;
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

import ca.corefacility.bioinformatics.irida.config.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.AssociatedProjectPage;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableList;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class AssociatedProjectsPageIT {
	private static final Logger logger = LoggerFactory.getLogger(AssociatedProjectsPageIT.class);
	public static final ImmutableList<String> ASSOCIATED_PROJECTS_WITH_RIGHTS = ImmutableList
			.of("project2", "project3");
	public static final String ASSOCIATED_PROJECT_NO_RIGHTS = "project5";

	AssociatedProjectPage page;

	private WebDriver driver;

	@Before
	public void setUp() {
		driver = new PhantomJSDriver();
		driver.manage().window().setSize(new Dimension(1024, 900));
		LoginPage loginPage = LoginPage.to(driver);
		loginPage.doLogin();

		page = new AssociatedProjectPage(driver);
	}

	@Test
	public void hasTheCorrectAssociatedProjects() {
		logger.debug("Testing: hasTheCorrectAssociatedProjects");
		List<String> projectsDiv = page.getAssociatedProjects();
		assertEquals("Has the correct number of associated projects", 3, projectsDiv.size());

		assertEquals("Has project with no rights", ASSOCIATED_PROJECT_NO_RIGHTS, page.getProjectWithNoRights());
		List<String> projectsWithRights = page.getProjectsWithRights();
		for (String project : ASSOCIATED_PROJECTS_WITH_RIGHTS) {
			assertTrue("Contains projects with authorization (" + project + ")", projectsWithRights.contains(project));
		}
	}
}
