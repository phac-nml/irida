package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSamplesPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.TableSummary;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 * Integration test to ensure that the Project Details Page.
 * </p>
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/ProjectSamplesView.xml")
public class ProjectSamplesPageIT extends AbstractIridaUIITChromeDriver {
	String FIRST_SAMPLE_NAME = "sample55422r";
	String SECOND_SAMPLE_NAME = "sample-5-fg-22";
	String THIRD_SAMPLE_NAME = "sample64565";

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

		assertEquals("Samples", page.getActivePage(), "Should have the project name as the page main header.");
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
		assertTrue(page.isSampleToolsAvailable(), "Sample Tools should be visible for a manager");
		page.openToolsDropDown();
		assertFalse(page.isMergeBtnEnabled(), "Merging requires more than 1 sample to be selected");
		assertFalse(page.isShareBtnEnabled(), "Sharing requires 1 or more samples to be selected");
		assertFalse(page.isRemoveBtnEnabled(), "Removing  requires 1 or more samples to be selected");
		page.closeToolsDropdown();

		page.openExportDropdown();
		assertFalse(page.isDownloadBtnEnabled(), "Downloading is only available when 1 or more samples are selected");
		assertTrue(page.isLinkerBtnEnabled(), "Linker button should be enabled whether samples are selected or not");
		assertFalse(page.isNcbiBtnEnabled(), "NCBI Export is only available when 1 or more samples are selected");

		page.closeExportDropdown();

		// Test with one sample selected
		page.selectSampleByName("sample55422r");
		page.openToolsDropDown();
		assertFalse(page.isMergeBtnEnabled(), "Merge option should not be enabled");
		assertTrue(page.isRemoveBtnEnabled(), "Remove option should be enabled");
		page.closeToolsDropdown();
		page.openExportDropdown();
		assertTrue(page.isDownloadBtnEnabled(), "Download option should be enabled");
		assertTrue(page.isNcbiBtnEnabled(), "NCBI Export option should be enabled");

		// Test with two samples selected
		page.selectSampleByName("sample-5-fg-22");
		page.openToolsDropDown();
		assertTrue(page.isMergeBtnEnabled(), "Merge option should be enabled");
		assertTrue(page.isShareBtnEnabled(), "Share option should be enabled");
		assertTrue(page.isRemoveBtnEnabled(), "Remove option should be enabled");
		page.openExportDropdown();
		assertTrue(page.isDownloadBtnEnabled(), "Download option should be enabled");
		assertTrue(page.isNcbiBtnEnabled(), "NCBI Export option should be enabled");
	}

	@Test
	public void testSampleSelection() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);
		TableSummary summary = page.getTableSummary();
		assertEquals(0, summary.getSelected(), "Should be 0 selected samples");
		assertEquals(23, summary.getTotal(), "Should be 0 selected samples");

		page.selectSampleByName("sample-5-fg-22");
		summary = page.getTableSummary();
		assertEquals(1, summary.getSelected(), "Should be 1 selected samples");

		page.toggleSelectAll();
		summary = page.getTableSummary();
		assertEquals(0, summary.getSelected(), "Should have all samples selected");

		page.toggleSelectAll();
		summary = page.getTableSummary();
		assertEquals(23, summary.getSelected(), "Should be 0 selected samples");
	}

	@Test
	public void testMergeSamples() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);

		TableSummary originalSummary = page.getTableSummary();
		String NEW_NAME = "I-AM-NEW-HERE";

		page.selectSampleByName(FIRST_SAMPLE_NAME);
		page.selectSampleByName(SECOND_SAMPLE_NAME);

		// Merge these samples with the original name
		page.mergeSamplesWithOriginalName(FIRST_SAMPLE_NAME);
		assertEquals(FIRST_SAMPLE_NAME, page.getMostRecentlyModifiedSampleName(),
				"Merged sample should have the original name");

		TableSummary finalSummary = page.getTableSummary();
		assertEquals(originalSummary.getTotal() - 1, finalSummary.getTotal(),
				"Should have one less sample after merge");

		// Merge with a new name
		page.selectSampleByName(FIRST_SAMPLE_NAME);
		page.selectSampleByName(THIRD_SAMPLE_NAME);
		page.mergeSamplesWithOriginalName(NEW_NAME);
		assertEquals(NEW_NAME, page.getMostRecentlyModifiedSampleName(), "Merged sample should have the new name");
	}

	@Test
	public void testRemoteProjectSamplesManagerSetup() {
		LoginPage.loginAsManager(driver());

		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 7);

		page.selectSampleByName("sample23p7");
		page.selectSampleByName("sample24p7");

		page.openToolsDropDown();
		assertTrue(page.isShareBtnEnabled(), "Share button should be enabled");
		assertFalse(page.isMergeBtnVisible(), "Merge button should not be displayed");
	}

	@Test
	public void testRemoveSamplesFromProject() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);
		TableSummary summary = page.getTableSummary();

		page.selectSampleByName(FIRST_SAMPLE_NAME);
		page.selectSampleByName(SECOND_SAMPLE_NAME);

		// Remove process
		page.removeSamples();
		TableSummary updatedSummary = page.getTableSummary();
		assertEquals(summary.getTotal() - 2, updatedSummary.getTotal(), "Should be 2 less samples after removal");
	}

	@Test
	public void testFilteringSamplesByProperties() {
		String NAME_FILTER_1 = "5";
		String NAME_FILTER_2 = "sample6";
		String ORGANISM_FILTER_1 = "Listeria";
		String ORGANISM_FILTER_2 = "E. coli";
		String ASSOCIATED_PROJECT_FILTER = "project5";


		LoginPage.loginAsManager(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);
		TableSummary summary = page.getTableSummary();
		assertEquals(23, summary.getTotal(), "Without the filter there should be 23 elements in the table");

		/*
		SAMPLE NAME FILTERING
		 */

		// Single name filter
		page.filterBySampleName(NAME_FILTER_1);
		summary = page.getTableSummary();
		assertEquals(19, summary.getTotal(), "Filtering the '5' should leave 19 samples in the table");

		page.filterBySampleName(NAME_FILTER_2);
		summary = page.getTableSummary();
		assertEquals(2, summary.getTotal(), "Filtering the '5' and 'sample6' should leave 4 samples in the table");

		// Test clear
		page.clearIndividualSampleNameFilter(NAME_FILTER_2);
		summary = page.getTableSummary();
		assertEquals(19, summary.getTotal(), "Removing a sample name filter");
		page.clearIndividualSampleNameFilter(NAME_FILTER_1);
		summary = page.getTableSummary();
		assertEquals(23, summary.getTotal(), "Removing all name filters should return to initial number of samples");

		/*
		ORGANISM FILTER
		 */
		page.filterByOrganism(ORGANISM_FILTER_1);
		summary = page.getTableSummary();
		assertEquals(2, summary.getTotal(), "Filtering by organism");
		page.clearIndividualOrganismFilter(ORGANISM_FILTER_1);

		/*
		ASSOCIATED PROJECTS
		 */
		page.toggleAssociatedProject(ASSOCIATED_PROJECT_FILTER);
		summary = page.getTableSummary();
		assertEquals(24, summary.getTotal(), "Should have more samples visible with another project selected");
		page.removeAssociatedProject(ASSOCIATED_PROJECT_FILTER);
		summary = page.getTableSummary();
		assertEquals(23, summary.getTotal(), "Should only display samples for the main project");

		/*
		TEST MULTIPLE FILTERS
		 */
		page.filterByOrganism(ORGANISM_FILTER_2);
		summary = page.getTableSummary();
		assertEquals(3, summary.getTotal(), "Filtering by organism");

		page.filterBySampleName("sample3");
		summary = page.getTableSummary();
		assertEquals(1, summary.getTotal(), "Filtering by only a name");

		page.clearIndividualOrganismFilter(ORGANISM_FILTER_2);
		summary = page.getTableSummary();
		assertEquals(3, summary.getTotal(), "Filtering by organism");
		page.clearIndividualSampleNameFilter("sample3");
		summary = page.getTableSummary();
		assertEquals(23, summary.getTotal(), "Filtering by organism");

		// TEST CREATED DATE
		page.filterByCreatedDate("2013-07-12", "2013-07-13");
		summary = page.getTableSummary();
		assertEquals(2, summary.getTotal(), "Filtering by created date");
		page.clearFilterByCreatedDate();
		summary = page.getTableSummary();
		assertEquals(23, summary.getTotal(), "Clearing created by filter");

		// TEST CREATED DATE
		page.filterByModifiedDate("2015-07-17", "2015-07-20");
		summary = page.getTableSummary();
		assertEquals(4, summary.getTotal(), "Filtering by modified date");
		page.clearFilterByModifiedDate();
		summary = page.getTableSummary();
		assertEquals(23, summary.getTotal(), "Clearing modified by filter");
	}

	@Test
	public void testCartFunctionality() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);

		// Select some samples
		page.selectSampleByName(FIRST_SAMPLE_NAME);
		page.selectSampleByName(SECOND_SAMPLE_NAME);
		page.addSelectedSamplesToCart();
		assertEquals(2, page.getCartCount(), "Should be two items in the cart");

		page.selectSampleByName(THIRD_SAMPLE_NAME);
		page.addSelectedSamplesToCart();
		assertEquals(3, page.getCartCount(), "Should be three items in the cart");
	}

	@Test
	public void testLinkerFunctionality() {
		LoginPage.loginAsManager(driver());
		ProjectSamplesPage page = ProjectSamplesPage.gotToPage(driver(), 1);

		assertEquals("ngsArchiveLinker.pl -p 1 -t fastq", page.getLinkerCommand(),
				"Should be the correct linker command");

		page.selectSampleByName(FIRST_SAMPLE_NAME);
		page.selectSampleByName(SECOND_SAMPLE_NAME);
		assertEquals("ngsArchiveLinker.pl -p 1 -s 16 -s 26 -t fastq", page.getLinkerCommand(),
				"Should display the correct linker for entire project");

		// Make sure that when the filter is applied, only the correct number of samples are selected.
		page.toggleSelectAll();
		assertEquals("ngsArchiveLinker.pl -p 1 -t fastq", page.getLinkerCommand(),
				"Should be the correct linker command");

		// Test with assembly
		assertEquals("ngsArchiveLinker.pl -p 1 -t fastq,assembly", page.getLinkerCommandWithAssembly(),
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