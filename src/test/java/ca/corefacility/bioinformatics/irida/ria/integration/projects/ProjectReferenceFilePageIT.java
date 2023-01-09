package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectReferenceFilePage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.jupiter.api.Assertions.*;

/**
 */
@Disabled
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/ProjectReferenceFileIT.xml")
public class ProjectReferenceFilePageIT extends AbstractIridaUIITChromeDriver {
	private static final Long PROJECT_ID_WITH_REFERENCE_FILES = 1L;
	private static final Long PROJECT_ID_WITHOUT_REFERENCE_FILES = 2L;

	@Test
	@Disabled
	public void testPageSetupUser() {
		// NON-MANAGER
		LoginPage.loginAsUser(driver());

		// 1. Without Files
		ProjectReferenceFilePage page_noFiles = ProjectReferenceFilePage
				.goTo(driver(), PROJECT_ID_WITHOUT_REFERENCE_FILES);
		assertTrue(page_noFiles.isNoFileNoticeDisplayed());
		assertTrue(page_noFiles.isNoFileNoticeUser());
		assertFalse(page_noFiles.isNoFileNoticeOwner());
		assertFalse(page_noFiles.isFilesTableDisplayed());
		// Ensure upload button not present
		assertFalse(page_noFiles.isUploadReferenceFileBtnPresent());

		// 2. With Files
		ProjectReferenceFilePage page_withFiles = ProjectReferenceFilePage
				.goTo(driver(), PROJECT_ID_WITH_REFERENCE_FILES);
		assertFalse(page_withFiles.isNoFileNoticeDisplayed());
		assertTrue(page_withFiles.isFilesTableDisplayed());
		assertEquals(2, page_withFiles.numRefFiles());
		assertTrue(page_withFiles.areDownloadFileBtnsAvailable());
		assertFalse(page_withFiles.areRemoveFileBtnsAvailable());

		// Ensure upload button not present
		assertFalse(page_noFiles.isUploadReferenceFileBtnPresent());

	}

	@Test
	@Disabled
	public void testPageSetupAdminManager() {
		// NON-MANAGER
		LoginPage.loginAsManager(driver());

		// 1. Without Files
		ProjectReferenceFilePage page_noFiles = ProjectReferenceFilePage
				.goTo(driver(), PROJECT_ID_WITHOUT_REFERENCE_FILES);
		assertTrue(page_noFiles.isNoFileNoticeDisplayed());
		assertTrue(page_noFiles.isNoFileNoticeOwner());
		assertFalse(page_noFiles.isNoFileNoticeUser());
		assertFalse(page_noFiles.isFilesTableDisplayed());
		// Ensure upload button present
		assertTrue(page_noFiles.isUploadReferenceFileBtnPresent());

		// 3. With Files
		ProjectReferenceFilePage page_withFiles = ProjectReferenceFilePage
				.goTo(driver(), PROJECT_ID_WITH_REFERENCE_FILES);
		assertFalse(page_withFiles.isNoFileNoticeDisplayed());
		assertTrue(page_withFiles.isFilesTableDisplayed());
		assertEquals(2, page_withFiles.numRefFiles());
		assertTrue(page_withFiles.areDownloadFileBtnsAvailable());
		assertTrue(page_withFiles.areRemoveFileBtnsAvailable());
		// Ensure upload button present
		assertTrue(page_noFiles.isUploadReferenceFileBtnPresent());
	}

	@Test
	@Disabled
	public void testRemoveReferenceFile() {
		LoginPage.loginAsManager(driver());
		ProjectReferenceFilePage page = ProjectReferenceFilePage.goTo(driver(), PROJECT_ID_WITH_REFERENCE_FILES);

		assertEquals(2, page.numRefFiles());
		page.removeFirstRefFile();
		assertEquals(1, page.numRefFiles());
	}
}
