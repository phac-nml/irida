package ca.corefacility.bioinformatics.irida.ria.integration.analysis;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowException;
import ca.corefacility.bioinformatics.irida.junit5.listeners.IntegrationUITestListener;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.TestAnalysis;
import ca.corefacility.bioinformatics.irida.model.workflow.config.IridaWorkflowIdSet;
import ca.corefacility.bioinformatics.irida.model.workflow.config.IridaWorkflowSet;
import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis.AnalysesUserPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis.AnalysisDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.FileUtilities;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowLoaderService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/analysis/AnalysisAdminView.xml")
public class AnalysisDetailsPageIT extends AbstractIridaUIITChromeDriver {
	private static final Logger logger = LoggerFactory.getLogger(AnalysisDetailsPageIT.class);
	private final FileUtilities fileUtilities = new FileUtilities();

	@Autowired
	@Qualifier("outputFileBaseDirectory")
	private Path outputFileBaseDirectory;

	@Autowired
	private IridaWorkflowLoaderService iridaWorkflowLoaderService;

	private final File DOWNLOADED_TREE_FILE = new File(IntegrationUITestListener.DOWNLOAD_DIRECTORY, "tree.svg");
	private final Path DOWNLOADED_TREE_PATH = Paths.get(DOWNLOADED_TREE_FILE.getPath());
	private final Path TEST_TREE_PATH = Paths.get("src/test/resources/files/tree.svg");

	@BeforeEach
	// Tree file used by multiple tests
	public void setFile() throws IOException {
		fileUtilities.copyFileToDirectory(outputFileBaseDirectory, "src/test/resources/files/snp_tree.tree");
	}

	@Test
	public void testAnalysisDetails() {
		LoginPage.loginAsManager(driver());
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 4L, "settings");
		assertTrue(page.compareTabTitle("Details"), "Page title should equal");
		assertEquals(7, page.getNumberOfListItems(), "There should be 7 labels for analysis details");
		// Analysis Description doesn't have a value
		assertEquals(6, page.getNumberOfListItemValues(), "There should be only 6 values for these labels");

		String[] expectedAnalysisDetails = new String[] {
				"My Completed Submission",
				"4",
				"SNVPhyl Phylogenomics Pipeline (1.0.1)",
				"MEDIUM",
				"Oct 6, 2013, 10:01 AM",
				"a few seconds" };
		assertTrue(page.analysisDetailsEqual(expectedAnalysisDetails),
				"The correct details are displayed for the analysis");
	}

	@Test
	public void testBioHanselOutput() throws IOException {
		fileUtilities.copyFileToDirectory(outputFileBaseDirectory, "src/test/resources/files/bio_hansel-results.json");

		LoginPage.loginAsManager(driver());

		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 12L, "");
		assertEquals(page.getTabContentTitle(), "Bio Hansel Information", "Page title should equal");

		assertTrue(page.expectedNumberOfListItemsEqualsActual(5), "Has 5 list items for Bio Hansel Information");

		page = AnalysisDetailsPage.initPage(driver(), 12L, "output");
		assertEquals(page.getTabContentTitle(), "Output File Preview", "Page title should equal");
		assertEquals(1, page.getNumberOfFilesDisplayed(), "There should be one output file");
		assertTrue(page.downloadAllFilesButtonVisible(), "There should be exactly one download all files button");
		assertTrue(page.downloadOutputFileButtonVisible(1),
				"There should be a download button for the file that is displayed");
	}

	@Test
	public void testDeleteAnalysis() {
		LoginPage.loginAsManager(driver());

		AnalysesUserPage analysesPage = AnalysesUserPage.initializeAdminPage(driver());
		analysesPage.clickPagination(2);
		assertEquals(6, analysesPage.getNumberOfAnalysesDisplayed(), "Should have 6 analyses displayed originally");

		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 9L, "delete");
		assertTrue(page.compareTabTitle("Delete Analysis"), "Page title should equal");
		assertTrue(page.deleteButtonExists());
		page.deleteAnalysis();

		analysesPage = AnalysesUserPage.initializeAdminPage(driver());
		page.clickPagination(2);
		assertEquals(5, analysesPage.getNumberOfAnalysesDisplayed(), "Should have 5 analyses displayed");
	}

	@Test
	public void testEditPriorityVisibility() throws URISyntaxException, IOException {
		LoginPage.loginAsManager(driver());
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 4L, "settings");
		assertTrue(page.compareTabTitle("Details"), "Page title should equal");

		// As this analysis is not in NEW state the
		// edit priority dropdown should not be visible
		assertFalse(page.priorityEditVisible(), "priority edit should be visible");

		page = AnalysisDetailsPage.initPage(driver(), 8L, "settings");
		assertTrue(page.compareTabTitle("Details"), "Page title should equal");
		// As this analysis is in NEW state the
		// edit priority dropdown should be visible
		assertTrue(page.priorityEditVisible(), "priority edit should be visible");
	}

	@Test
	public void testOutputFiles() throws IOException {
		fileUtilities.copyFileToDirectory(outputFileBaseDirectory, "src/test/resources/files/filterStats.txt");

		LoginPage.loginAsManager(driver());

		// Has output files
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 4L, "output");
		assertTrue(page.compareTabTitle("Output File Preview"), "Page title should equal");
		assertEquals(2, page.getNumberOfFilesDisplayed(), "There should be two output files");
		assertTrue(page.downloadAllFilesButtonVisible(), "There should be exactly one download all files button");
		assertTrue(page.downloadIndividualFilesMenuButtonVisible(),
				"There should be exactly one download individual files dropdown button");
		assertTrue(page.downloadIndividualFilesMenuVisible(),
				"There should be exactly one download individual files dropdown menu");
		assertTrue(page.downloadOutputFileButtonVisible(2),
				"There should be a download button for the files that are displayed");

		// Has no output files
		page = AnalysisDetailsPage.initPage(driver(), 10L, "output");
		assertTrue(page.compareTabTitle("Output File Preview"), "Page title should equal");
		assertEquals(0, page.getNumberOfFilesDisplayed(), "There should be no output files");
		assertEquals("No outputs available to display", page.getWarningAlertText(), "Has a no output files alert");
	}

	@Test
	public void testPageSetUp() throws URISyntaxException, IOException {
		logger.debug("Testing 'Analysis Details Page'");

		LoginPage.loginAsManager(driver());
		// Submissions with trees and not sistr or biohansel
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 4L, "");
		assertEquals(page.getTabContentTitle(), "Tree Viewer", "Page title should equal");
		assertTrue(page.hasHorizontalTabLinks(), "Has horizontal tab links");

		// Completed submission should not display steps component
		assertTrue(!page.analysisStepsVisible(),
				"Analysis steps are not visible since the analysis is in completed state");

		// Submissions without trees and not sistr or biohansel
		page = AnalysisDetailsPage.initPage(driver(), 6L, "");
		assertEquals(page.getTabContentTitle(), "Output File Preview", "Page title should equal");
		assertTrue(page.hasHorizontalTabLinks(), "Has horizontal tab links");

		// Any other submission state should display steps component
		page = AnalysisDetailsPage.initPage(driver(), 2L, "");
		assertTrue(page.analysisStepsVisible(),
				"Analysis steps are visible since the analysis isn't in completed state");
	}

	@Test
	// Has no specific results output tab (for example tree, biohansel, sistr) so the output file preview
	// page is the default view
	public void testPipelineResultsWithoutSpecialTab() throws IOException {
		fileUtilities.copyFileToDirectory(outputFileBaseDirectory,
				"src/test/resources/files/refseq-masher-matches.tsv");

		LoginPage.loginAsManager(driver());

		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 13L, "");
		assertTrue(page.compareTabTitle("Output File Preview"), "Page title should equal");

		assertEquals(1, page.getNumberOfFilesDisplayed(), "There should be one output file");
		assertTrue(page.downloadAllFilesButtonVisible(), "There should be exactly one download all files button");
		assertTrue(page.downloadOutputFileButtonVisible(1),
				"There should be a download button for the file that is displayed");
	}

	@Test
	public void testProvenance() throws IOException {
		fileUtilities.copyFileToDirectory(outputFileBaseDirectory, "src/test/resources/files/filterStats.txt");
		LoginPage.loginAsManager(driver());

		// Has output files so display a provenance
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 4L, "provenance");
		assertTrue(page.compareTabTitle("Provenance"), "Page title should equal");
		assertEquals(2, page.getProvenanceFileCount(), "There should be two files");
		page.getFileProvenance(0);
		assertEquals(1, page.getToolCount(), "Should have 1 tool associated with filterStats");
		page.displayToolExecutionParameters();
		assertEquals(2, page.getGalaxyParametersCount(), "Tool should have 2 parameters");

		page.getFileProvenance(1);
		assertEquals(2, page.getToolCount(), "Should have 2 tools associated with the tree");
		page.displayToolExecutionParameters();
		assertEquals(1, page.getGalaxyParametersCount(), "First tool should have 1 parameter");

		// We click the first file again and check for tools and execution parameters
		page.getFileProvenance(0);
		assertEquals(1, page.getToolCount(), "Should have 1 tool associated with filterStats");
		page.displayToolExecutionParameters();
		assertEquals(2, page.getGalaxyParametersCount(), "Tool should have 2 parameter");

		// Has no output files so no provenance displayed
		page = AnalysisDetailsPage.initPage(driver(), 10L, "provenance");
		assertTrue(page.compareTabTitle("Provenance"), "Page title should equal");
		assertEquals(0, page.getProvenanceFileCount(), "There should be no file");
		assertEquals("Unable to display provenance as no output files were found for analysis.",
				page.getWarningAlertText(), "Has a no provenance available alert");
	}

	@Test
	public void testSamplesPage() {
		LoginPage.loginAsManager(driver());
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 4L, "settings/samples");
		assertTrue(page.compareTabTitle("Samples"), "Page title should equal");
		assertEquals(2, page.getNumberOfSamplesInAnalysis(), "Should display 2 pairs of paired end files");

		// Filter samples by search string
		page.filterSamples("01-");
		assertEquals(1, page.getNumberOfSamplesInAnalysis(), "Should display 1 pair of paired end files");

		// Download reference file button if file exists
		assertTrue(page.referenceFileDownloadButtonVisible(), "Should have a download reference file button");
	}

	@Test
	public void testSharedProjects() {
		LoginPage.loginAsManager(driver());
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 4L, "settings/share");
		assertTrue(page.compareTabTitle("Manage Results"), "Page title should equal");
		assertTrue(page.hasSharedWithProjects(), "Analysis shared projects");

		page.removeSharedProjects();
		assertTrue(!page.hasSharedWithProjects(), "Analysis no longer shared with any projects");

		page.addSharedProjects();
		assertTrue(page.hasSharedWithProjects(), "Analysis shared with projects");
	}

	@Test
	public void testSistrOutput() throws IOException {
		fileUtilities.copyFileToDirectory(outputFileBaseDirectory,
				"src/test/resources/files/sistr-predictions-pass.json");

		LoginPage.loginAsManager(driver());

		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 11L, "");
		assertEquals(page.getTabContentTitle(), "SISTR Information", "Page title should equal");
		assertTrue(page.hasSideBarTabLinks(), "Has vertical tabs for SISTR results and output files");

		assertTrue(page.expectedNumberOfListItemsEqualsActual(9), "Has 9 list items for SISTR Information");

		page = AnalysisDetailsPage.initPage(driver(), 11L, "sistr/cgmlst");
		assertEquals(page.getTabContentTitle(), "cgMLST330", "Page title should equal");
		assertTrue(page.expectedNumberOfListItemsEqualsActual(7), "Has 7 list items for cgMLST330");

		page = AnalysisDetailsPage.initPage(driver(), 11L, "sistr/mash");
		assertEquals(page.getTabContentTitle(), "Mash", "Page title should equal");
		assertTrue(page.expectedNumberOfListItemsEqualsActual(4), "Has 4 list items for Mash");

		page = AnalysisDetailsPage.initPage(driver(), 11L, "sistr/citation");
		assertEquals(page.getTabContentTitle(), "Citation", "Page title should equal");
		assertTrue(page.citationVisible(), "Page has a citation");

		page = AnalysisDetailsPage.initPage(driver(), 11L, "output");
		assertEquals(page.getTabContentTitle(), "Output File Preview", "Page title should equal");
		assertEquals(1, page.getNumberOfFilesDisplayed(), "There should be one output file");
		assertTrue(page.downloadAllFilesButtonVisible(), "There should be exactly one download all files button");
		assertTrue(page.downloadOutputFileButtonVisible(1),
				"There should be a download button for the file that is displayed");
	}

	@Test
	// Successfully completed analysis (COMPLETED state)
	public void testTabRoutingAnalysisCompleted() throws URISyntaxException, IOException {
		LoginPage.loginAsManager(driver());

		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 4L, "");
		assertEquals(page.getTabContentTitle(), "Tree Viewer", "Page title should equal");

		page = AnalysisDetailsPage.initPage(driver(), 4L, "output");
		assertEquals(page.getTabContentTitle(), "Output File Preview", "Page title should equal");

		page = AnalysisDetailsPage.initPage(driver(), 4L, "provenance");
		assertEquals(page.getTabContentTitle(), "Provenance", "Page title should equal");

		page = AnalysisDetailsPage.initPage(driver(), 4L, "settings");
		assertTrue(page.compareTabTitle("Details"), "Page title should equal");

		page = AnalysisDetailsPage.initPage(driver(), 4L, "settings/details");
		assertTrue(page.compareTabTitle("Details"), "Page title should equal");

		page = AnalysisDetailsPage.initPage(driver(), 4L, "settings/samples");
		assertTrue(page.compareTabTitle("Samples"), "Page title should equal");

		page = AnalysisDetailsPage.initPage(driver(), 4L, "settings/share");
		assertTrue(page.compareTabTitle("Manage Results"), "Page title should equal");

		page = AnalysisDetailsPage.initPage(driver(), 4L, "settings/delete");
		assertTrue(page.compareTabTitle("Delete Analysis"), "Page title should equal");
	}

	@Test
	//Analysis which did not complete successfully (ERROR State)
	public void testTabRoutingAnalysisError() throws URISyntaxException, IOException {
		LoginPage.loginAsManager(driver());
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 7L, "");

		assertTrue(page.jobErrorAlertVisible(), "No job error information available alert visible");

		// Should not be able to view output files page if analysis errored
		page = AnalysisDetailsPage.initPage(driver(), 7L, "output");
		assertFalse(page.menuIncludesItem("Output Files"), "Should not have an output tab");

		// Should not be able to view provenance page if analysis errored
		page = AnalysisDetailsPage.initPage(driver(), 7L, "provenance");
		assertFalse(page.menuIncludesItem("Provenance"), "Should not have a provenance tab");

		page = AnalysisDetailsPage.initPage(driver(), 7L, "settings");
		assertTrue(page.compareTabTitle("Details"), "Page title should equal");

		page = AnalysisDetailsPage.initPage(driver(), 7L, "settings");
		assertTrue(page.compareTabTitle("Details"), "Page title should equal");

		page = AnalysisDetailsPage.initPage(driver(), 7L, "settings/samples");
		assertTrue(page.compareTabTitle("Samples"), "Page title should equal");

		// Should not be able to share results if analysis errored
		page = AnalysisDetailsPage.initPage(driver(), 7L, "settings/share");
		assertFalse(page.menuIncludesItem("Manage Results"), "Should not have a Manage Results tab");

		page = AnalysisDetailsPage.initPage(driver(), 7L, "settings/delete");
		assertTrue(page.compareTabTitle("Delete Analysis"), "Page title should equal");
	}

	@Test
	void testTreeOutput() throws IOException {
		LoginPage.loginAsManager(driver());

		// Has tree file
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 4L, "");
		assertEquals("Tree Viewer", page.getTabContentTitle(), "Page title should equal");

		page.openDownloadDropdown();
		page.downloadTreeSVG();

		// Compare with existing tree
		assertTrue(fileUtilities.compareByMemoryMappedFiles(DOWNLOADED_TREE_PATH, TEST_TREE_PATH));


		// Make sure all buttons are working
		assertEquals("rc", page.getCurrentlyDisplayedTreeShapeIcon(), "Rectangle should be the default shape of the tree");
		page.openTreeShapeDropdown();
		assertTrue(page.areAllTreeShapeOptionsDisplayed(), "Should display all possible tree types");
		assertEquals("Rectangular", page.getCurrentTreeShapeTitleAttr());
		page.updateTreeShape("dg");
		assertEquals("dg", page.getCurrentlyDisplayedTreeShapeIcon(), "Diagonal should be the default shape of the tree");
		page.openTreeShapeDropdown();
		assertEquals("Diagonal", page.getCurrentTreeShapeTitleAttr());


		page.openMetadataDropdown();
		assertEquals(4, page.getNumberOfMetadataFields());
		page.openMetadataTemplateSelect();

		page.openLegend();
		assertTrue(page.legendContainsCorrectAmountOfMetadataFields());
	}

	@Test
	public void testUnknownPipelineOutput() throws IOException, URISyntaxException, IridaWorkflowException {
		IridaWorkflowsService iridaWorkflowsService;
		IridaWorkflow unknownWorkflow;

		// Register an UNKNOWN workflow
		Path workflowVersion1DirectoryPath = Paths
				.get(TestAnalysis.class.getResource("workflows/TestAnalysis/1.0").toURI());

		iridaWorkflowsService = new IridaWorkflowsService(new IridaWorkflowSet(Sets.newHashSet()),
				new IridaWorkflowIdSet(Sets.newHashSet()));

		unknownWorkflow = iridaWorkflowLoaderService.loadIridaWorkflowFromDirectory(workflowVersion1DirectoryPath);
		logger.debug("Registering workflow: " + unknownWorkflow.toString());
		iridaWorkflowsService.registerWorkflow(unknownWorkflow);

		fileUtilities.copyFileToDirectory(outputFileBaseDirectory, "src/test/resources/files/snp_tree_2.tree");

		LoginPage.loginAsManager(driver());

		// Has an UNKNOWN analysis type so the view should default to the Output File Preview page.
		// This submission is setup with refseq_masher parameters and output file
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 14L, "");
		assertTrue(page.compareTabTitle("Output File Preview"), "Page title should equal");

		assertEquals(1, page.getNumberOfFilesDisplayed(), "There should be one output file");
		assertTrue(page.downloadAllFilesButtonVisible(), "There should be exactly one download all files button");
		assertTrue(page.downloadOutputFileButtonVisible(1),
				"There should be a download button for the file that is displayed");

		page = AnalysisDetailsPage.initPage(driver(), 14L, "provenance");
		assertTrue(page.compareTabTitle("Provenance"), "Page title should equal");
		assertEquals(1, page.getProvenanceFileCount(), "There should be one file");
		page.getFileProvenance(0);
		assertEquals(1, page.getToolCount(), "Should have 2 tools associated with the tree");
		page.displayToolExecutionParameters();
		assertEquals(2, page.getGalaxyParametersCount(), "First tool should have 2 parameter");

		page = AnalysisDetailsPage.initPage(driver(), 14L, "settings");
		assertTrue(page.compareTabTitle("Details"), "Page title should equal");
		assertEquals(7, page.getNumberOfListItems(), "There should be 7 labels for analysis details");
		// Analysis Description doesn't have a value
		assertEquals(6, page.getNumberOfListItemValues(), "There should be only 6 values for these labels");

		String[] expectedAnalysisDetails = new String[] {
				"My Completed Submission UNKNOWN PIPELINE",
				"14",
				"Unknown Pipeline (Unknown Version)",
				"MEDIUM",
				"Oct 6, 2013, 10:01 AM",
				"a few seconds" };
		assertTrue(page.analysisDetailsEqual(expectedAnalysisDetails),
				"The correct details are displayed for the analysis");
	}

	@Test
	public void testUpdateEmailPipelineResultVisibility() throws URISyntaxException, IOException {
		LoginPage.loginAsManager(driver());
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 4L, "settings");
		assertTrue(page.compareTabTitle("Details"), "Page title should equal");
		// As this analysis is in COMPLETED state the
		// Receive Email Upon Pipeline Completion section
		// and Receive Email Upon Pipeline Error section
		// should not be visible
		assertFalse(page.emailPipelineResultStatusSelectVisible(), "email pipeline result status should be visible");

		page = AnalysisDetailsPage.initPage(driver(), 8L, "settings");
		assertTrue(page.compareTabTitle("Details"), "Page title should equal");
		// As this analysis is not in COMPLETED state the
		// Receive Email Upon Pipeline Completion section
		// and Receive Email Upon Pipeline Completion section
		// should be visible
		assertTrue(page.emailPipelineResultStatusSelectVisible(), "email pipeline result status should be visible");
	}

	@Test
	public void testGalaxyHistoryIdNotVisibleOnError() {
		// Regular user should not have a clickable link to the galaxy history
		LoginPage.loginAsUser(driver());
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 16L, "");
		assertFalse(page.galaxyHistoryIdVisible(), "Galaxy History Id link should not be displayed");
	}

	@Test
	public void testGalaxyHistoryIdVisibleOnError() {
		// Admin user should have a clickable link to the galaxy history
		LoginPage.loginAsAdmin(driver());
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 15L, "");
		assertTrue(page.galaxyHistoryIdVisible(), "Galaxy History Id link should not be displayed");
	}

}
