package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSamplesPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

/**
 * <p>
 * Integration test to ensure that the Project Details Page.
 * </p>
 *
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/ProjectSamplesView.xml")
public class ProjectSamplesPageIT extends AbstractIridaUIITChromeDriver {
	private static final Logger logger = LoggerFactory.getLogger(ProjectSamplesPageIT.class);

	@Before
	public void init() {
		LoginPage.loginAsManager(driver());
	}

	@Test(expected = AssertionError.class)
	public void testGoingToInvalidPage() {
		logger.debug("Testing going to an invalid sample id");
		ProjectSamplesPage.gotToPage(driver(), 100);
	}

	@Test
	public void testPageSetUp() {
		logger.info("Testing page set up for: Project Samples");
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);

		assertTrue("Should have the project name as the page main header.", page.getTitle().equals("project"));
		assertEquals("Should display 10 projects initially.", 10, page.getNumberProjectsDisplayed());

		// Test the status of all buttons.
		assertFalse("Merge Button should be disabled", page.isMergeBtnEnabled());
		assertFalse("Copy Button should be disabled", page.isCopyBtnEnabled());
		assertFalse("Move Button should be disabled", page.isMoveBtnEnabled());
		assertFalse("Remove Button should be disabled", page.isRemoveBtnEnabled());

		// Test toolbar when changes selected sample count to 1.
		page.selectSample(0);
		assertFalse("Merge Button should be disabled", page.isMergeBtnEnabled());
		assertTrue("Copy Button should be enabled", page.isCopyBtnEnabled());
		assertTrue("Move Button should be enabled", page.isMoveBtnEnabled());
		assertTrue("Remove Button should be enabled", page.isRemoveBtnEnabled());

		// Test toolbar when changes selected sample count to 2.
		page.selectSample(1);
		assertTrue("Merge Button should be enabled", page.isMergeBtnEnabled());
		assertTrue("Copy Button should be enabled", page.isCopyBtnEnabled());
		assertTrue("Move Button should be enabled", page.isMoveBtnEnabled());
		assertTrue("Remove Button should be enabled", page.isRemoveBtnEnabled());

		// Need to ensure they return to there default state when unchecked
		page.selectSample(1);
		assertFalse("Merge Button should be disabled", page.isMergeBtnEnabled());
		assertTrue("Copy Button should be enabled", page.isCopyBtnEnabled());
		assertTrue("Move Button should be enabled", page.isMoveBtnEnabled());
		assertTrue("Remove Button should be enabled", page.isRemoveBtnEnabled());
		page.selectSample(0);
		assertFalse("Merge Button should be disabled", page.isMergeBtnEnabled());
		assertFalse("Copy Button should be disabled", page.isCopyBtnEnabled());
		assertFalse("Move Button should be disabled", page.isMoveBtnEnabled());
		assertFalse("Remove Button should be disabled", page.isRemoveBtnEnabled());

		// Test select all/none
		page.selectAllOrNone();
		assertTrue("Merge Button should be enabled", page.isMergeBtnEnabled());
		assertTrue("Copy Button should be enabled", page.isCopyBtnEnabled());
		assertTrue("Move Button should be enabled", page.isMoveBtnEnabled());
		assertTrue("Remove Button should be enabled", page.isRemoveBtnEnabled());
		page.selectAllOrNone();
		assertFalse("Merge Button should be disabled", page.isMergeBtnEnabled());
		assertFalse("Copy Button should be disabled", page.isCopyBtnEnabled());
		assertFalse("Move Button should be disabled", page.isMoveBtnEnabled());
		assertFalse("Remove Button should be disabled", page.isRemoveBtnEnabled());

	}

	@Test
	public void testPaging() {
		logger.info("Testing paging for: Project Samples");
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);

		assertFalse("'Previous' button should be disabled", page.isPreviousBtnEnabled());
		assertTrue("'Next' button should be enabled", page.isNextBtnEnabled());
		assertEquals("Should be 3 pages of samples", 3, page.getPaginationCount());
	}

	@Test
	public void testSampleSelection() {
		logger.info("Testing sample selection for: Project Samples");
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);
		assertEquals("Should be 0 selected samples", "No samples selected", page.getSelectedInfoText());

		page.selectSample(0);
		page.selectSampleWithShift(4);
		assertEquals("Should be 5 selected samples", "5 Samples Selected", page.getSelectedInfoText());

		page.selectAllOrNone();
		// Again, this is only the count for the current page!
		assertEquals("Should be 21 selected samples", "21 Samples Selected", page.getSelectedInfoText());
		page.selectAllOrNone();
		assertEquals("Should be 0 selected samples", "No samples selected", page.getSelectedInfoText());

	}

	@Test
	public void testAddSamplesToCart() {
		logger.info("Testing adding samples to the global cart.");
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);
		page.selectSample(0);
		page.selectSampleWithShift(4);
		assertEquals("Should be 5 selected samples", "5 Samples Selected", page.getSelectedInfoText());

		page.addSelectedSamplesToCart();
		assertEquals("Should be 5 samples in the cart", 5, page.getCartCount());

	}

	// TODO: (Josh - 2016-02-05) Create testing for merge

	// TODO: (Josh - 2016-02-05) Create testing for renaming merge

	// TODO: (Josh - 2016-02-05) Create testing for copying and moving (as user and admin)

	// TODO: (Josh - 2016-02-05) Create testing for copying samples as manager to unmanaged project

	@Test
	public void testRemoveSamplesFromProject() {
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);
		assertFalse("Remove button should be disabled since no samples selected", page.isRemoveBtnEnabled());

		// Select some samples
		page.selectSample(0);
		page.selectSample(1);
		assertEquals("Should be 2 selected samples", "2 Samples Selected", page.getSelectedInfoText());
		assertTrue("Remove button should be enabled since 2 samples selected", page.isRemoveBtnEnabled());

		// Remove process
		page.removeSamples();
		assertFalse("Remove button should be disabled since no samples selected", page.isRemoveBtnEnabled());
		assertEquals("Should be only 2 pages of projects now", 2, page.getPaginationCount());
		page.selectPaginationPage(2);
		assertEquals("Should only be displaying 9 samples.", 9, page.getNumberProjectsDisplayed());
		assertEquals("Should be 0 selected samples", "No samples selected", page.getSelectedInfoText());
	}

	// TODO: (Josh - 2016-02-05) Create test for export linker

	// TODO: (Josh - 2016-02-05) Create test for filtering samples

	// TODO: (Josh - 2016-02-05) Create tests for cart functionality.
}
