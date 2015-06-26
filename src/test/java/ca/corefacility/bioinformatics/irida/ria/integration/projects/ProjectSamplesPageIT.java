package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.AssociatedProjectEditPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSamplesPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.RemoteApiUtilities;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.ImmutableList;

/**
 * <p>
 * Integration test to ensure that the Project Details Page.
 * </p>
 *
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/ProjectSamplesView.xml")
public class ProjectSamplesPageIT extends AbstractIridaUIITChromeDriver {
	private static final Logger logger = LoggerFactory.getLogger(ProjectSamplesPageIT.class);

	private ProjectSamplesPage page;

	@Before
	public void setUpTest() {
		this.page = new ProjectSamplesPage(driver());
	}

	@Test(expected = AssertionError.class)
	public void testGoingToInvalidPage() {
		logger.debug("Testing going to an invalid sample id");
		LoginPage.loginAsManager(driver());
		page.goToPage("112423");
	}

	@Test(expected = AssertionError.class)
	public void testGoingToNonLongProjectId() {
		logger.debug("Testing going to an invalid sample id");
		LoginPage.loginAsManager(driver());
		page.goToPage("not_a_long");
		String wait = "wat";
	}

	@Test
	public void testInitialPageSetUp() {
		logger.info("Testing page set up for: Project Samples");
		LoginPage.loginAsManager(driver());
		page.goToPage();
		assertTrue(page.getTitle().contains("Samples"));
		assertEquals(10, page.getNumberOfSamplesDisplayed());

		page.showSamplesDropdownMenu();
		assertFalse("Merge should be disabled", page.isSampleMergeOptionEnabled());
		assertFalse("Copy should be disabled", page.isSampleCopyOptionEnabled());
		assertFalse("Move should be disabled", page.isSampleMoveOptionEnabled());
		assertFalse("Remove should be disabled", page.isSampleRemoveOptionEnabled());

		// Check when selecting a sample
		page.selectSampleByRow(1);
		page.showSamplesDropdownMenu();
		assertFalse("Merge should be disabled", page.isSampleMergeOptionEnabled());
		assertTrue("Copy should be enabled", page.isSampleCopyOptionEnabled());
		assertTrue("Move should be enabled", page.isSampleMoveOptionEnabled());
		assertTrue("Remove should be enabled", page.isSampleRemoveOptionEnabled());

		// Check when selecting a second sample
		page.selectSampleByRow(2);
		page.showSamplesDropdownMenu();
		assertTrue("Merge should be enabled", page.isSampleMergeOptionEnabled());
		assertTrue("Copy should be enabled", page.isSampleCopyOptionEnabled());
		assertTrue("Move should be enabled", page.isSampleMoveOptionEnabled());
		assertTrue("Remove should be enabled", page.isSampleRemoveOptionEnabled());
	}

	@Test
	public void testPaging() {
		logger.info("Testing paging for: Project Samples");
		LoginPage.loginAsManager(driver());
		page.goToPage();

		// Initial setup
		assertFalse(page.isFirstButtonEnabled());
		assertFalse(page.isPreviousButtonEnabled());
		assertTrue(page.isNextButtonEnabled());
		assertTrue(page.isLastButtonEnabled());
		assertEquals(1, page.getGetSelectedPageNumber());

		// Second Page
		page.selectPage(2);
		assertEquals(2, page.getGetSelectedPageNumber());
		assertTrue(page.isFirstButtonEnabled());
		assertTrue(page.isPreviousButtonEnabled());
		assertTrue(page.isNextButtonEnabled());
		assertTrue(page.isLastButtonEnabled());
		assertEquals(10, page.getNumberOfSamplesDisplayed());

		// Third Page (1 element)
		page.selectPage(3);
		assertTrue(page.isFirstButtonEnabled());
		assertTrue(page.isPreviousButtonEnabled());
		assertFalse(page.isNextButtonEnabled());
		assertFalse(page.isLastButtonEnabled());
		assertEquals(3, page.getGetSelectedPageNumber());
		assertEquals(1, page.getNumberOfSamplesDisplayed());

		// Previous Button
		page.clickPreviousPageButton();
		assertEquals(2, page.getGetSelectedPageNumber());
		page.clickPreviousPageButton();
		assertEquals(1, page.getGetSelectedPageNumber());

		// Next Button
		page.clickNextPageButton();
		assertEquals(2, page.getGetSelectedPageNumber());
		page.clickNextPageButton();
		assertEquals(3, page.getGetSelectedPageNumber());

		// First and List page buttons
		page.clickFirstPageButton();
		assertEquals(1, page.getGetSelectedPageNumber());
		assertFalse(page.isFirstButtonEnabled());
		page.clickLastPageButton();
		assertEquals(3, page.getGetSelectedPageNumber());
		assertFalse(page.isLastButtonEnabled());
		assertTrue(page.isFirstButtonEnabled());
		assertEquals(1, page.getNumberOfSamplesDisplayed());
	}

	@Test
	public void testSelectSamples() {
		logger.info("Testing selecting samples for: Project Samples");
		LoginPage.loginAsManager(driver());
		page.goToPage();

		assertEquals(0, page.getNumberOfSamplesSelected());
		selectFirstThreeSamples();
		assertEquals(3, page.getNumberOfSamplesSelected());
		page.selectSampleByRow(1);
		assertEquals(2, page.getNumberOfSamplesSelected());
	}

	@Test
	public void testPagingWithSelectingSamples() {
		logger.info("Testing paging with selecting samples for: Project Samples");
		List<Integer> page1 = ImmutableList.of(0, 1, 6);
		LoginPage.loginAsManager(driver());
		page.goToPage();

		assertEquals(0, page.getNumberOfSamplesSelected());
		page1.forEach(page::selectSampleByRow);
		assertEquals(3, page.getNumberOfSamplesSelected());
		assertTrue(page.isRowSelected(6));

		// Let's go to the second page
		page.clickNextPageButton();
		for (int row : page1) {
			assertFalse(page.isRowSelected(row));
		}
		assertEquals(0, page.getNumberOfSamplesSelected());
		page.selectSampleByRow(2);

		// Let's jump around a little
		jumpAroundLists();

		// Make sure samples are still selected on the first page
		page.selectPage(1);
		for (int row : page1) {
			assertTrue(page.isRowSelected(row));
		}
		assertEquals(3, page.getNumberOfSamplesSelected());

		// Deselect first page samples
		page1.forEach(page::selectSampleByRow);
		assertEquals(0, page.getNumberOfSamplesSelected());

		jumpAroundLists();

		page.selectPage(1);
		assertEquals(0, page.getNumberOfSamplesSelected());
	}

	@Test
	public void testSelectedSampleCount() {
		LoginPage.loginAsManager(driver());
		page.goToPage();
		assertEquals(0, page.getTotalSelectedSamplesCount());
		page.selectSampleByRow(0);
		page.selectSampleByRow(1);
		assertEquals(2, page.getTotalSelectedSamplesCount());
		page.clickNextPageButton();
		assertEquals(2, page.getTotalSelectedSamplesCount());
		page.selectSampleByRow(5);
		assertEquals(3, page.getTotalSelectedSamplesCount());
		page.clickLastPageButton();
		assertEquals(3, page.getTotalSelectedSamplesCount());
		page.selectSampleByRow(0);
		assertEquals(4, page.getTotalSelectedSamplesCount());
		page.selectSampleByRow(0);
		assertEquals(3, page.getTotalSelectedSamplesCount());
		page.clickFirstPageButton();
		assertEquals(3, page.getTotalSelectedSamplesCount());
		page.selectSampleByRow(0);
		page.selectSampleByRow(1);
		assertEquals(1, page.getTotalSelectedSamplesCount());
		page.clickLastPageButton();
		assertEquals(1, page.getTotalSelectedSamplesCount());
	}

	@Test
	public void testDefaultMerge() {
		LoginPage.loginAsManager(driver());
		page.goToPage();
		assertEquals(0, page.getTotalSelectedSamplesCount());

		page.selectSampleByRow(0);
		page.selectSampleByRow(1);
		assertEquals(2, page.getTotalSelectedSamplesCount());
		assertTrue(page.isBtnEnabled("samplesOptionsBtn"));
		page.clickBtn("samplesOptionsBtn");
		page.clickBtn("mergeBtn");
		assertTrue(page.isItemVisible("merge-samples-modal"));
		page.clickBtn("confirmMergeBtn");
		assertTrue(page.checkSuccessNotification());
		assertEquals(0, page.getTotalSelectedSamplesCount());
	}

	@Test
	public void testRenameMerge() {
		LoginPage.loginAsManager(driver());
		page.goToPage();
		assertEquals(0, page.getTotalSelectedSamplesCount());

		page.selectSampleByRow(0);
		page.selectSampleByRow(1);
		assertEquals(2, page.getTotalSelectedSamplesCount());
		page.clickBtn("samplesOptionsBtn");
		page.clickBtn("mergeBtn");
		assertTrue(page.isItemVisible("merge-samples-modal"));

		// Try entering a name that is too short
		assertTrue(page.isBtnEnabled("confirmMergeBtn"));
		page.enterNewMergeSampleName("HI");
		assertTrue(page.isItemVisible("merge-length-error"));
		assertFalse(page.isBtnEnabled("confirmMergeBtn"));

		// Try entering a name with spaces
		page.enterNewMergeSampleName("HI BOB I AM WRONG");
		assertTrue(page.isItemVisible("merge-format-error"));
		assertFalse(page.isBtnEnabled("confirmMergeBtn"));

		// Try to enter a proper name name
		String oriName = page.getSampleNameByRow(0);
		String newLongName = "LONGERNAME";
		page.enterNewMergeSampleName(newLongName);
		assertFalse(page.isItemVisible("merge-length-error"));
		assertFalse(page.isItemVisible("merge-format-error"));
		assertTrue(page.isBtnEnabled("confirmMergeBtn"));
		page.clickBtn("confirmMergeBtn");
		assertTrue(page.checkSuccessNotification());
		String updatedName = page.getSampleNameByRow(0);
		assertFalse(oriName.equals(updatedName));
		assertTrue(updatedName.equals(newLongName));
	}

	@Test
	public void testProjectUserCannotCopyOrMoveFilesToAnotherProject() {
		LoginPage.loginAsUser(driver());
		page.goToPage();
		assertFalse(page.isElementOnScreen("copyBtn"));
		assertFalse(page.isElementOnScreen("moveBtn"));
	}

	@Test
	public void testCopySamplesAsManagerToManagedProject() {
		LoginPage.login(driver(), "project1Manager", "Password1");
		// Make sure the project to copy to is empty to begin with
		page.goToPage("2");
		assertEquals(0, page.getNumberOfSamplesDisplayed());

		page.goToPage();
		assertTrue(page.isElementOnScreen("copyBtn"));
		assertTrue(page.isElementOnScreen("moveBtn"));

		// Should be able to copy files to a project that they are a manager of.
		selectFirstThreeSamples();
		page.clickBtn("samplesOptionsBtn");
		page.clickBtn("copyBtn");
		assertTrue(page.isItemVisible("copy-samples-modal"));
		page.selectProjectByName("2", "confirm-copy-samples");
		assertTrue(page.isBtnEnabled("confirm-copy-samples"));
		page.clickBtn("confirm-copy-samples");
		page.checkSuccessNotification();

		// Check to make sure the samples where copied there
		page.goToPage("2");
		assertEquals(3, page.getNumberOfSamplesDisplayed());
	}

	@Test
	public void testMoveSamplesAsManagerToManagedProject() {
		LoginPage.login(driver(), "project1Manager", "Password1");
		// Make sure the project to copy to is empty to begin with
		page.goToPage("2");
		assertEquals(0, page.getNumberOfSamplesDisplayed());
		page.goToPage();

		// Should be able to copy files to a project that they are a manager of.
		selectFirstThreeSamples();
		page.clickBtn("samplesOptionsBtn");
		page.clickBtn("moveBtn");
		assertTrue(page.isItemVisible("move-samples-modal"));
		page.selectProjectByName("2", "confirm-move-samples");
		assertTrue(page.isBtnEnabled("confirm-move-samples"));
		page.clickBtn("confirm-move-samples");
		page.checkSuccessNotification();

		assertEquals("no samples shold be selected after move", 0, page.getTotalNumberOfSamplesSelected());

		// Check to make sure the samples where copied there
		page.goToPage("2");
		assertEquals(3, page.getNumberOfSamplesDisplayed());
	}

	@Test
	public void testCopySamplesAsManagerToUnmanagedProject() {
		LoginPage.login(driver(), "project1Manager", "Password1");
		page.goToPage();

		// Should be able to copy files to a project that they are a manager of.
		selectFirstThreeSamples();
		page.clickBtn("samplesOptionsBtn");
		page.clickBtn("copyBtn");
		assertTrue(page.isItemVisible("copy-samples-modal"));
		page.selectProjectByName("3", "confirm-copy-samples");
		assertFalse("Since the project does not exist in the list, they cannot copy files to it.",
				page.isBtnEnabled("confirm-copy-samples"));
	}

	@Test
	public void testRemoveSamples() {
		LoginPage.login(driver(), "project1Manager", "Password1");
		page.goToPage();

		int totalSampleCount = page.getTotalSampleCount();

		selectFirstThreeSamples();
		page.clickBtn("samplesOptionsBtn");
		page.clickBtn("removeBtn");
		assertTrue(page.isItemVisible("remove-samples-modal"));
		page.clickBtn("confirmRemoveBtn");
		assertTrue(page.checkSuccessNotification());

		page.goToPage();
		int newSampleCount = page.getTotalSampleCount();

		assertEquals("should be 3 less samples that we started with", totalSampleCount - 3, newSampleCount);
	}

	@Test
	public void testAdminCopyFromAnyProjectToAnyProject() {
		LoginPage.loginAsManager(driver());
		page.goToPage();

		selectFirstThreeSamples();
		// Admin is not on project5
		page.clickBtn("samplesOptionsBtn");
		page.clickBtn("copyBtn");
		assertTrue(page.isItemVisible("copy-samples-modal"));
		page.selectProjectByName("5", "confirm-copy-samples");
		assertTrue(page.isBtnEnabled("confirm-copy-samples"));
		page.clickBtn("confirm-copy-samples");
		assertTrue(page.checkSuccessNotification());

		// Check to make sure the samples where copied there
		page.goToPage("5");
		assertEquals(3, page.getNumberOfSamplesDisplayed());
	}

	@Test
	public void testMoveSampleToProjectConflict() {
		LoginPage.loginAsManager(driver());
		page.goToPage();

		// try to move to existing project
		page.selectSampleByRow(0);
		page.clickBtn("samplesOptionsBtn");
		page.clickBtn("moveBtn");
		assertTrue(page.isItemVisible("move-samples-modal"));
		page.selectProjectByName("3", "confirm-move-samples");
		assertTrue(page.isBtnEnabled("confirm-move-samples"));
		page.clickBtn("confirm-move-samples");
		assertTrue(page.checkWarningNotification());

		assertEquals(1, page.getTotalNumberOfSamplesSelected());
	}

	@Test
	public void testMultiSelection() {
		LoginPage.loginAsManager(driver());
		page.goToPage();

		// Test selecting a page
		assertEquals(0, page.getTotalNumberOfSamplesSelected());
		page.clickBtn("selectBtn");
		page.clickBtn("selectPageBtn");
		assertEquals(10, page.getTotalNumberOfSamplesSelected());

		// Test clearing the selections
		page.clickBtn("selectBtn");
		page.clickBtn("selectNoneBtn");
		assertEquals(0, page.getTotalNumberOfSamplesSelected());

		// Test select all
		page.clickBtn("selectBtn");
		page.clickBtn("selectAllBtn");
		assertEquals(21, page.getTotalNumberOfSamplesSelected());

		// Test clearing again
		page.clickBtn("selectBtn");
		page.clickBtn("selectNoneBtn");
		assertEquals(0, page.getTotalNumberOfSamplesSelected());

		// Select random samples on one page and then all on the second
		selectFirstThreeSamples();
		assertEquals(3, page.getTotalNumberOfSamplesSelected());
		page.clickNextPageButton();
		page.clickBtn("selectBtn");
		page.clickBtn("selectPageBtn");
		assertEquals(13, page.getTotalNumberOfSamplesSelected());
		page.clickBtn("selectBtn");
		page.clickBtn("selectAllBtn");
		assertEquals(21, page.getTotalNumberOfSamplesSelected());
	}

	@Test
	public void testExportLinker() {
		LoginPage.loginAsManager(driver());
		page.goToPage();

		assertFalse(page.isBtnEnabled("exportOptionsBtn"));
		page.selectSampleByRow(0);
		assertTrue(page.isBtnEnabled("exportOptionsBtn"));
		page.clickBtn("exportOptionsBtn");
		page.clickBtn("exportLinkerBtn");

		assertTrue(page.isItemVisible("linker-modal"));
		assertEquals(1, getSampleFlagCount(page.getLinkerScriptText()));
		page.clickBtn("linkerCloseBtn");

		// Select all samples
		page.clickBtn("selectBtn");
		page.clickBtn("selectAllBtn");
		page.clickBtn("exportOptionsBtn");
		page.clickBtn("exportLinkerBtn");
		assertEquals(0, getSampleFlagCount(page.getLinkerScriptText()));
		page.clickBtn("linkerCloseBtn");

		page.selectSampleByRow(0);
		int selectedCount = page.getTotalSelectedSamplesCount();
		page.clickBtn("exportOptionsBtn");
		page.clickBtn("exportLinkerBtn");
		String command = page.getLinkerScriptText();
		assertEquals(selectedCount, getSampleFlagCount(command));
	}

	@Test
	public void testTableSorts() {
		LoginPage.loginAsManager(driver());
		page.goToPage();

		// Page should be sorted by creation date first
		assertTrue(page.isTableSortedAscByCreationDate());
		page.sortTableByCreatedDate();
		assertFalse(page.isTableSortedAscByCreationDate());
		assertTrue(page.isTableSortedDescByCreationDate());

		// Sort by name
		page.sortTableByName();
		assertTrue(page.isTableSortedDescBySampleName());
		page.sortTableByName();
		assertFalse(page.isTableSortedDescBySampleName());
		assertTrue(page.isTableSortedAscBySampleName());
	}

	@Test
	public void testSampleFilter() {
		LoginPage.loginAsManager(driver());
		page.goToPage();

		// Filter by name
		page.filterByName("ple1");
		assertEquals(1, page.getFilteredSampleCount());
		page.filterByName("5");
		assertEquals(17, page.getFilteredSampleCount());
		page.filterByName(" ");

		// Filter by organism
		page.filterByOrganism("coli");
		assertEquals(3, page.getFilteredSampleCount());
		page.filterByOrganism("Listeria");
		assertEquals(2, page.getFilteredSampleCount());
	}

	@Test
	public void testChangingTableSize() {
		LoginPage.loginAsManager(driver());
		page.goToPage();

		assertEquals(10, page.getNumberOfSamplesDisplayed());
		page.selectPageSize("25");
		assertEquals(21, page.getNumberOfSamplesDisplayed());
		page.selectPageSize("10");
		assertEquals(10, page.getNumberOfSamplesDisplayed());
	}

	@Test
	public void testCart() {
		LoginPage.loginAsManager(driver());
		page.goToPage();

		selectFirstThreeSamples();
		page.addSamplesToGlobalCart();
		assertEquals(3, page.getCartCount());
		assertEquals(1, page.getCartProjectCount());

		// Ensure that this is persisted across pages.
		page.goToPage("5");
		assertEquals(3, page.getCartCount());
		assertEquals(1, page.getCartProjectCount());
		page.clickBtn("cart-show-btn");
		page.clickBtn("go-to-pipeline-btn");
		assertTrue(driver().getCurrentUrl().contains("/pipelines"));
	}

	@Test
	public void testClearCart() {
		LoginPage.loginAsManager(driver());
		page.goToPage();

		selectFirstThreeSamples();
		page.addSamplesToGlobalCart();
		page.showCart();
		assertEquals("cart should have 3 samples", 3, page.getCartCount());
		assertEquals("cart should have 1 project", 1, page.getCartProjectCount());

		page.clearCart();

		assertEquals("cart should have been emptied", 0, page.getCartCount());
		assertEquals("cart should have been emptied", 0, page.getCartProjectCount());
	}

	@Test
	public void testDeleteProjectFromCart() {
		LoginPage.loginAsManager(driver());
		page.goToPage();

		assertFalse(page.isCartVisible());
		assertEquals(0, page.getCartCount());

		selectFirstThreeSamples();
		page.addSamplesToGlobalCart();
		page.showCart();
		assertEquals("cart should have 3 samples", 3, page.getCartCount());
		assertEquals("cart should have 1 project", 1, page.getCartProjectCount());

		page.removeProjectFromCart(1L);

		assertEquals("cart should have been emptied", 0, page.getCartCount());
		assertEquals("cart should have been emptied", 0, page.getCartProjectCount());
	}

	@Test
	public void testDeleteSampleFromCart() {
		LoginPage.loginAsManager(driver());
		page.goToPage();

		selectFirstThreeSamples();
		page.addSamplesToGlobalCart();
		page.showCart();
		assertEquals("cart should have 3 samples", 3, page.getCartCount());
		assertEquals("cart should have 1 project", 1, page.getCartProjectCount());

		page.removeFirstSampleFromProjectInCart(1L);

		assertEquals("cart should have 2 samples", 2, page.getCartCount());
		assertEquals("cart should have 2 samples", 1, page.getCartProjectCount());
	}

	@Test
	public void testShowAssociatedSamples() throws InterruptedException {
		LoginPage.loginAsManager(driver());
		page.goToPage("6");
		int initialNumber = page.getNumberOfSamplesDisplayed();

		page.enableAssociatedProjects();

		int laterNumber = page.getNumberOfSamplesDisplayed();

		assertNotEquals("page should have associated samples displayed", initialNumber, laterNumber);
	}

	@Test
	public void testShowRemoteSamples() throws InterruptedException {
		LoginPage.loginAsAdmin(driver());
		// add the api
		RemoteApiUtilities.addRemoteApi(driver());

		// associate a project from that api
		AssociatedProjectEditPage apEditPage = new AssociatedProjectEditPage(driver());
		apEditPage.goTo(2L);
		apEditPage.viewRemoteTab();
		apEditPage.clickAssociatedButton(6L);
		apEditPage.checkNotyStatus("success");

		// go to project
		page.goToPage("2");

		assertEquals("no remote samples should be displayed", 0, page.getNumberOfRemoteSamplesDisplayed());

		page.enableRemoteProjects();

		assertEquals("1 remote sample sould be displayed", 1, page.getNumberOfRemoteSamplesDisplayed());

		page.selectSampleByClass("remote-sample");
		page.addSamplesToGlobalCart();
		assertEquals(1, page.getCartCount());
	}

	private int getSampleFlagCount(String command) {
		Pattern pattern = Pattern.compile("-s");
		Matcher matcher = pattern.matcher(command);
		int count = 0;
		while (matcher.find()) {
			count++;
		}
		return count;
	}

	private void selectFirstThreeSamples() {
		page.selectSampleByRow(0);
		page.selectSampleByRow(1);
		page.selectSampleByRow(2);
	}

	private void jumpAroundLists() {
		page.selectPage(1);
		page.selectPage(3);
		page.selectPage(2);
		page.selectPage(1);
		page.selectPage(2);
	}
}
