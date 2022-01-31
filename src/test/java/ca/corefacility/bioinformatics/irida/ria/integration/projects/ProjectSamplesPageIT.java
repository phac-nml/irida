package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSamplesPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.Lists;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 * Integration test to ensure that the Project Details Page.
 * </p>
 *
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/ProjectSamplesView.xml")
public class ProjectSamplesPageIT extends AbstractIridaUIITChromeDriver {
	@AfterEach
	public void resetTable() {
		/*
		This was added to ensure that after every test the samples table is returned to its default
		state.  Current DataTables stores a reference to which page in the table the user is on.
		 */
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);
		page.closeModalIfOpen();
		page.selectPaginationPage(1);
	}

	@Test
	public void testGoingToInvalidPage() {
		LoginPage.loginAsManager(driver());

		assertThrows(AssertionError.class, () -> {
			ProjectSamplesPage.gotToPage(driver(), 100);
		});
	}

	@Test
	public void testPageSetUp() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);

		assertTrue(page.getActivePage().equals("Samples"), "Should have the project name as the page main header.");
		assertEquals(10, page.getNumberProjectsDisplayed(), "Should display 10 projects initially.");
	}

	@Test
	public void testToolbarButtonsAsCollaborator() {
		LoginPage.loginAsUser(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);
		assertFalse(page.isSampleToolsAvailable(), "Sample Tools should be hidden from a collaborator");
	}

	@Test
	public void testToolbarButtonsAsManager() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);

		// Test set up with no sample selected
		page.openToolsDropDown();
		assertFalse(page.isMergeBtnEnabled(), "Merge option should not be enabled");
		assertFalse(page.isRemoveBtnEnabled(), "Remove option should not be enabled");
		page.closeToolsDropdown();
		page.openExportDropdown();
		assertFalse(page.isDownloadBtnEnabled(), "Download option should not be enabled");
		assertFalse(page.isNcbiBtnEnabled(), "NCBI Export option should not be enabled");

		// Test with one sample selected
		page.selectSample(0);
		page.openToolsDropDown();
		assertFalse(page.isMergeBtnEnabled(), "Merge option should not be enabled");
		assertTrue(page.isRemoveBtnEnabled(), "Remove option should be enabled");
		page.closeToolsDropdown();
		page.openExportDropdown();
		assertTrue(page.isDownloadBtnEnabled(), "Download option should be enabled");
		assertTrue(page.isNcbiBtnEnabled(), "NCBI Export option should be enabled");

		// Test with two samples selected
		page.selectSample(1);
		page.openToolsDropDown();
		assertTrue(page.isMergeBtnEnabled(), "Merge option should be enabled");
		assertTrue(page.isShareButtonAvailable(), "Share option should be enabled");
		assertTrue(page.isRemoveBtnEnabled(), "Remove option should be enabled");
		page.openExportDropdown();
		assertTrue(page.isDownloadBtnEnabled(), "Download option should be enabled");
		assertTrue(page.isNcbiBtnEnabled(), "NCBI Export option should be enabled");
	}

	@Test
	public void testPaging() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);

		assertFalse(page.isPreviousBtnEnabled(), "'Previous' button should be disabled");
		assertTrue(page.isNextBtnEnabled(), "'Next' button should be enabled");
		assertEquals(3, page.getPaginationCount(), "Should be 3 pages of samples");
	}

	@Test
	public void testAssociatedProjects() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);
		assertEquals("Showing 1 to 10 of 23 entries", page.getTableInfo(), "Should be displaying 23 samples");
		page.displayAssociatedProject();
		assertEquals("Showing 1 to 10 of 24 entries", page.getTableInfo(), "Should be displaying 24 samples");
	}

	@Test
	public void testSampleSelection() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);
		assertEquals("No samples selected", page.getSelectedInfoText(), "Should be 0 selected samples");

		page.selectSample(0);
		assertEquals("1 sample selected", page.getSelectedInfoText(), "Should be 1 selected samples");

		page.selectSampleWithShift(4);
		assertEquals("5 samples selected", page.getSelectedInfoText(), "Should be 5 selected samples");

		page.selectAllSamples();
		assertEquals("23 samples selected", page.getSelectedInfoText(), "Should have all samples selected");

		page.deselectAllSamples();
		assertEquals("No samples selected", page.getSelectedInfoText(), "Should be 0 selected samples");
	}

	@Test
	public void testAddSamplesToCart() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);
		page.selectSample(0);
		page.selectSampleWithShift(4);
		assertEquals("5 samples selected", page.getSelectedInfoText(), "Should be 5 selected samples");


		page.goToNextPage();
		page.selectSample(1);
		page.selectSample(2);
		assertEquals("7 samples selected", page.getSelectedInfoText(), "Should be 7 selected samples");

		page.addSelectedSamplesToCart();
		assertEquals(7, page.getCartCount(), "Should be 7 samples in the cart");
		page.selectPaginationPage(1);

		// Need to make sure select all samples works
		page.selectAllSamples();
		page.addSelectedSamplesToCart();
		assertEquals(23, page.getCartCount(), "Should be 23 samples in the cart");
	}

	@Test
	public void testMergeSamples() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);
		// Select some samples
		page.selectSample(0);
		page.selectSample(2);
		assertEquals("2 samples selected", page.getSelectedInfoText(), "Should be 2 selected samples");

		// Merge these samples with the original name
		List<String> originalNames = Lists.newArrayList(page.getSampleNamesOnPage().get(0),
				page.getSampleNamesOnPage().get(2));
		page.mergeSamplesWithOriginalName();
		List<String> mergeNames = Lists.newArrayList(page.getSampleNamesOnPage().get(0),
				page.getSampleNamesOnPage().get(2));
		assertEquals(originalNames.get(0), mergeNames.get(0), "Should still the first samples name");
		assertFalse(originalNames.get(1).equals(mergeNames.get(1)),
				"Should have different sample second since it was merged");

		// Merge with a new name
		page.selectSample(0);
		page.selectSample(1);
		String newSampleName = "NEW_NAME";
		page.mergeSamplesWithNewName(newSampleName);
		String name = page.getSampleNamesOnPage().get(0);
		assertEquals(newSampleName, name, "Should have the new sample name");
	}

	@Test
	public void testRemoteSampleManagerButtonDisabled() {
		LoginPage.loginAsManager(driver());

		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 7);
		page.selectSample(0);
		page.selectSample(1);

		page.waitUntilShareButtonVisible();
		assertTrue(page.isShareButtonAvailable(), "Share button should be enabled");
		assertFalse(page.isMergeBtnEnabled(), "Merge button should not be enabled");
	}

	@Test
	public void testRemoveSamplesFromProject() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);

		// Select some samples
		page.selectSample(0);
		page.selectSample(1);

		// Remove process
		page.removeSamples();
		assertEquals(3, page.getPaginationCount(), "Should be only 3 pages of projects now");
		page.selectPaginationPage(2);
		assertEquals(10, page.getNumberProjectsDisplayed(), "Should only be displaying 10 samples.");
		assertEquals("No samples selected", page.getSelectedInfoText(), "Should be 0 selected samples");
	}

	@Test
	public void testFilteringSamplesByProperties() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);
		assertEquals("Showing 1 to 10 of 23 entries", page.getTableInfo(), "Should have 23 projects displayed");
		page.filterByName("5");
		assertEquals("Showing 1 to 10 of 19 entries", page.getTableInfo(), "Should have 19 projects displayed");
		page.filterByName("52");
		assertEquals("Showing 1 to 3 of 3 entries", page.getTableInfo(), "Should have 3 projects displayed");

		// Make sure that when the filter is applied, only the correct number of samples are selected.
		page.selectAllSamples();
		assertEquals("3 samples selected", page.getSelectedInfoText(), "Should only have 3 samples selected");

		// Test clearing the filters
		page.clearFilter();
		assertEquals("Showing 1 to 10 of 23 entries", page.getTableInfo(), "Should have 23 projects displayed");

		// Should ignore case
		page.filterByName("sample");
		assertEquals("Showing 1 to 10 of 23 entries", page.getTableInfo(), "Should ignore case when filtering");

		// Test date range filter
		page.clearFilter();
		assertEquals("Showing 1 to 10 of 23 entries", page.getTableInfo(), "Should have 23 samples displayed");

		// Should find sample with underscores not hyphens
		page.filterByName("sample_5_fg_22");
		assertEquals(2, page.getSampleNamesOnPage().size(), "Should only have returned 2 sample");
		assertEquals("sample_5_fg_22", page.getSampleNamesOnPage().get(0), "Should have sample with exact name");

		// Should find sample with hyphens not underscores
		page.filterByName("sample-5-fg-22");
		assertEquals(1, page.getSampleNamesOnPage().size(), "Should only have returned 1 sample");
		assertEquals("sample-5-fg-22", page.getSampleNamesOnPage().get(0), "Should have sample with exact name");

	}

	@Test
	public void testFilteringWithDates() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);
		page.filterByDateRange("07/06/2015 - 07/09/2015");
		assertEquals("Showing 1 to 4 of 4 entries", page.getTableInfo(), "Should ignore case when filtering");

		// Make sure that when the filter is applied, only the correct number of samples are selected.
		page.selectAllSamples();
		assertEquals("4 samples selected", page.getSelectedInfoText(), "Should only have 4 samples selected after filter");

		// Test clearing the filters
		page.clearFilter();
		assertEquals("Showing 1 to 10 of 23 entries", page.getTableInfo(), "Should have 23 samples displayed");
	}

	@Test
	public void testCartFunctionality() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);

		// Select some samples
		page.selectSample(0);
		page.selectSample(1);

		// Add them to the cart
		page.addSelectedSamplesToCart();
		assertEquals(2, page.getCartCount(), "Should be two items in the cart");

		page.selectSample(5);
		page.addSelectedSamplesToCart();
		assertEquals(3, page.getCartCount(), "Should be three items in the cart");
	}

	@Test
	public void testLinkerFunctionalityForProject() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);

		page.openLinkerModal();
		assertEquals("ngsArchiveLinker.pl -p 1 -t fastq", page.getLinkerText(),
				"Should display the correct linker for entire project");
	}

	@Test
	public void testLinkerFunctionalityForSamples() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);

		page.filterByDateRange("07/06/2015 - 07/09/2015");
		assertEquals("Showing 1 to 4 of 4 entries", page.getTableInfo(), "Should ignore case when filtering");

		// Make sure that when the filter is applied, only the correct number of samples are selected.
		page.selectAllSamples();
		assertEquals("4 samples selected", page.getSelectedInfoText(), "Should only have 4 samples selected after filter");

		// Open the linker modal
		page.openLinkerModal();
		assertEquals("ngsArchiveLinker.pl -p 1 -s 9 -s 8 -s 7 -s 6 -t fastq", page.getLinkerText(),
				"Should display the correct linker command");

	}

	@Test
	public void testLinkerFunctionalityForFiletype() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);

		page.openLinkerModal();
		page.clickLinkerFileType("assembly");
		assertEquals("ngsArchiveLinker.pl -p 1 -t fastq,assembly", page.getLinkerText(),
				"Should display the correct linker for entire project");
	}

	@Test
	public void testAddNewSamples() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);
		page.openCreateNewSampleModal();
		page.enterSampleName("BAD");
		assertTrue(page.isSampleNameErrorDisplayed(), "Should show a warning message");
		page.enterSampleName("BAD NAME");
		assertTrue(page.isSampleNameErrorDisplayed(), "Should show a warning message");
		page.enterSampleName("BAD ***");
		assertTrue(page.isSampleNameErrorDisplayed(), "Should show a warning message");
		page.enterSampleName("GOOD_NAME");
		assertFalse(page.isSampleNameErrorDisplayed(), "Sample name error should not be displayed");
	}
}
