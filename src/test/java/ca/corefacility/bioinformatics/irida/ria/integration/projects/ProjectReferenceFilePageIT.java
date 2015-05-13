package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.TestUtilities;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/ProjectReferenceFileIT.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class ProjectReferenceFilePageIT {
	private static final Long PROJECT_ID_WITH_REFERENCE_FILES = 1L;
	private static final Long PROJECT_ID_WITHOUT_REFERENCE_FILES = 2L;
	private WebDriver driver;

	@Before
	public void setUp() {
		this.driver = TestUtilities.setDriverDefaults(new PhantomJSDriver());
	}

	@After
	public void destroy() {
		this.driver.quit();
	}

	@Test
	public void testPageSetupUser() {
		// NON-MANAGER
		LoginPage.loginAsUser(driver);

		// 1. Without Files
		ProjectReferenceFilePage page_noFiles = ProjectReferenceFilePage
				.goTo(driver, PROJECT_ID_WITHOUT_REFERENCE_FILES);
		assertTrue(page_noFiles.isNoFileNoticeDisplayed());
		assertFalse(page_noFiles.isNoFileNoticeOwner());
		assertFalse(page_noFiles.isFilesTableDisplayed());
		// Ensure upload button not present
		assertFalse(page_noFiles.isUploadReferenceFileBtnPresent());

		// 2. With Files
		ProjectReferenceFilePage page_withFiles = ProjectReferenceFilePage
				.goTo(driver, PROJECT_ID_WITH_REFERENCE_FILES);
		assertFalse(page_withFiles.isNoFileNoticeDisplayed());
		assertTrue(page_withFiles.isFilesTableDisplayed());
		assertEquals(2, page_withFiles.numRefFiles());
		assertFalse(page_withFiles.areRemoveFileBtnsAvailable());

		// Ensure upload button not present
		assertFalse(page_noFiles.isUploadReferenceFileBtnPresent());

	}

	@Test
	public void testPageSetupAdminManager() {
		// NON-MANAGER
		LoginPage.loginAsManager(driver);

		// 1. Without Files
		ProjectReferenceFilePage page_noFiles = ProjectReferenceFilePage
				.goTo(driver, PROJECT_ID_WITHOUT_REFERENCE_FILES);
		assertTrue(page_noFiles.isNoFileNoticeDisplayed());
		assertTrue(page_noFiles.isNoFileNoticeOwner());
		assertFalse(page_noFiles.isFilesTableDisplayed());
		// Ensure upload button present
		assertTrue(page_noFiles.isUploadReferenceFileBtnPresent());

		// 3. With Files
		ProjectReferenceFilePage page_withFiles = ProjectReferenceFilePage
				.goTo(driver, PROJECT_ID_WITH_REFERENCE_FILES);
		assertFalse(page_withFiles.isNoFileNoticeDisplayed());
		assertTrue(page_withFiles.isFilesTableDisplayed());
		assertTrue(page_withFiles.areRemoveFileBtnsAvailable());
		assertEquals(2, page_withFiles.numRefFiles());
		// Ensure upload button present
		assertTrue(page_noFiles.isUploadReferenceFileBtnPresent());
	}

	@Test
	public void testRemoveReferenceFile() {
		LoginPage.loginAsManager(driver);
		ProjectReferenceFilePage page = ProjectReferenceFilePage.goTo(driver, PROJECT_ID_WITH_REFERENCE_FILES);

		assertEquals(2, page.numRefFiles());
		page.removeFirstRefFile();
		assertEquals(1, page.numRefFiles());
	}
}
