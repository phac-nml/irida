package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.ria.integration.drivers.IridaPhantomJSDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectMetadataPage;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

/**
 * <p> Integration test to ensure that the Project Details Page. </p>
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
public class ProjectMetadataPageIT {
	private final String PAGE_TITLE = "IRIDA Platform - project - Metadata";
	private final Long PROJECT_ID_OWNER = 1L;
	private final Long PROJECT_ID_COLLABORATOR = 6L;
	private final String PROJECT_NAME = "project";
	private final String PROJECT_DESCRIPTION = "This is an interesting project description.";
	private final String PROJECT_ORGANISM = "E. coli";
	private final String PROJECT_REMOTE_URL = "http://google.ca";
	private final String REFERENCE_FILE_NAME = "02-2222_S1_L001_R1_001.fastq";

	private WebDriver driver;
	private ProjectMetadataPage page;

	@Before
	public void setUp() {
		driver = new IridaPhantomJSDriver();
		LoginPage.loginAsAdmin(driver);
		page = new ProjectMetadataPage(driver);
	}

	@After
	public void destroy() {
		if (driver != null) {
			driver.close();
			driver.quit();
		}
	}

	@Test
	public void displaysTheProjectMetaData() {
		driver.get("http://localhost:8080/projects/" + PROJECT_ID_OWNER + "/metadata");
		assertEquals("Displays the correct page title", PAGE_TITLE, driver.getTitle());
		assertEquals("Displays the correct project name", PROJECT_NAME, page.getDataProjectName());
		assertEquals("Displays the correct description", PROJECT_DESCRIPTION, page.getDataProjectDescription());
		assertEquals("Displays the correct organism", PROJECT_ORGANISM, page.getDataProjectOrganism());
		assertEquals("Displays the correct remoteURL", PROJECT_REMOTE_URL, page.getDataProjectRemoteURL());
		assertEquals("Display reference files", 1, page.getReferenceFileCount());

		page.clickReferenceFilesTab();
		assertEquals("Displays the name of the reference file", REFERENCE_FILE_NAME, page.getReferenceFileName());

		assertTrue("Contains edit metadata button", page.hasEditButton());

		// Should not have edit button on project that is not owner of.
		driver.get("http://localhost:8080/projects/" + PROJECT_ID_COLLABORATOR + "/metadata");
		assertFalse("Should not contain the edit medtadata button if they are only a collaborator",
				page.hasEditButton());
	}

}
