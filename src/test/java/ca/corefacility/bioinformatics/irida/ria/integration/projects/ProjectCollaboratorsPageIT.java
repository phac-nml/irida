package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.ProjectCollaboratorsPage;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

import java.util.List;

/**
 * <p>
 * Integration test to ensure that the Project Collaborators Page.
 * </p>
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class ProjectCollaboratorsPageIT {
	public static final Long PROJECT_ID = 1L;
	private WebDriver driver;
	private ProjectCollaboratorsPage collaboratorsPage;

    private static final ImmutableList<String> COLLABORATORS_NAMES = ImmutableList.of(
            "Mr. Manager",
            "test User"
    );

	@Before
	public void setUp() {
		this.driver = new PhantomJSDriver();
		LoginPage loginPage = LoginPage.to(driver);
		loginPage.doLogin();

		collaboratorsPage = new ProjectCollaboratorsPage(driver, PROJECT_ID);
	}

	@After
	public void destroy() {
		if (driver != null) {
			driver.close();
		}
	}

	@Test
	public void testPageSetUp() {
		assertEquals("Page h1 tag is properly set.", "project Collaborators", collaboratorsPage.getTitle());
        List<String> names = collaboratorsPage.getProjectCollaboratorsNames();
        for (String name : names) {
            assertTrue("Has the correct collaborators names", COLLABORATORS_NAMES.contains(name));
        }
    }
}
