package ca.corefacility.bioinformatics.irida.ria.integration.analysis;

import java.io.IOException;
import java.net.URISyntaxException;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowException;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.TestAnalysis;
import ca.corefacility.bioinformatics.irida.model.workflow.config.IridaWorkflowIdSet;
import ca.corefacility.bioinformatics.irida.model.workflow.config.IridaWorkflowSet;
import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis.AnalysesUserPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis.AnalysisDetailsPage;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowLoaderService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.Sets;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import ca.corefacility.bioinformatics.irida.ria.integration.utilities.FileUtilities;

@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/analysis/AnalysisAdminView.xml")
public class AnalysisDetailsPageIT extends AbstractIridaUIITChromeDriver {
	private static final Logger logger = LoggerFactory.getLogger(AnalysisDetailsPageIT.class);
	private FileUtilities fileUtilities = new FileUtilities();

	@Autowired
	@Qualifier("outputFileBaseDirectory")
	private Path outputFileBaseDirectory;

	@Autowired
	private IridaWorkflowLoaderService iridaWorkflowLoaderService;

	@Before
	// Tree file used by multiple tests
	public void setFile() throws IOException {
		fileUtilities.copyFileToDirectory(outputFileBaseDirectory, "src/test/resources/files/snp_tree.tree");
	}

	@Test
	public void testAnalysisDetails() {
		LoginPage.loginAsManager(driver());
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 4L, "settings/details");
		assertTrue("Page title should equal", page.compareTabTitle("Details"));
		assertEquals("There should be 7 labels for analysis details", 7, page.getNumberOfListItems());
		// Analysis Description doesn't have a value
		assertEquals("There should be only 6 values for these labels", 6, page.getNumberOfListItemValues());

		String expectedAnalysisDetails[] = new String[] { "My Completed Submission", "4",
				"SNVPhyl Phylogenomics Pipeline (1.0.1)", "MEDIUM", "Oct 6, 2013 10:01 AM", "a few seconds" };
		assertTrue("The correct details are displayed for the analysis",
				page.analysisDetailsEqual(expectedAnalysisDetails));
	}

	@Test
	public void testBioHanselOutput() throws IOException {
		fileUtilities.copyFileToDirectory(outputFileBaseDirectory, "src/test/resources/files/bio_hansel-results.json");

		LoginPage.loginAsManager(driver());

		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 12L, "");
		assertTrue("Page title should equal", page.comparePageTitle("Bio Hansel Information"));

		assertTrue("Has 5 list items for Bio Hansel Information", page.expectedNumberOfListItemsEqualsActual(5));

		page = AnalysisDetailsPage.initPage(driver(), 12L, "output");
		assertTrue("Page title should equal", page.comparePageTitle("Output File Preview"));
		assertEquals("There should be one output file", 1, page.getNumberOfFilesDisplayed());
		assertTrue("There should be exactly one download all files button", page.downloadAllFilesButtonVisible());
		assertTrue("There should be a download button for the file that is displayed",
				page.downloadOutputFileButtonVisible());
	}

	@Test
	public void testDeleteAnalysis() {
		LoginPage.loginAsManager(driver());

		AnalysesUserPage analysesPage = AnalysesUserPage.initializeAdminPage(driver());
		analysesPage.clickPagination(2);
		assertEquals("Should have 4 analyses displayed originally", 4, analysesPage.getNumberOfAnalysesDisplayed());

		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 9L, "settings/delete");
		assertTrue("Page title should equal", page.compareTabTitle("Delete Analysis"));
		assertTrue(page.deleteButtonExists());
		page.deleteAnalysis();

		analysesPage = AnalysesUserPage.initializeAdminPage(driver());
		page.clickPagination(2);
		assertEquals("Should have 3 analyses displayed", 3, analysesPage.getNumberOfAnalysesDisplayed());
	}

	@Test
	public void testEditPriorityVisibility() throws URISyntaxException, IOException {
		LoginPage.loginAsManager(driver());
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 4L, "settings/details");
		assertTrue("Page title should equal", page.compareTabTitle("Details"));

		// As this analysis is not in NEW state the
		// edit priority dropdown should not be visible
		assertFalse("priority edit should be visible", page.priorityEditVisible());

		page = AnalysisDetailsPage.initPage(driver(), 8L, "settings/details");
		assertTrue("Page title should equal", page.compareTabTitle("Details"));
		// As this analysis is in NEW state the
		// edit priority dropdown should be visible
		assertTrue("priority edit should be visible", page.priorityEditVisible());
	}

	@Test
	public void testOutputFiles() {
		LoginPage.loginAsManager(driver());

		// Has output files
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 4L, "output");
		assertTrue("Page title should equal", page.compareTabTitle("Output File Preview"));
		assertEquals("There should be one output file", 1, page.getNumberOfFilesDisplayed());
		assertTrue("There should be exactly one download all files button", page.downloadAllFilesButtonVisible());
		assertTrue("There should be exactly one download individual files dropdown button", page.downloadIndividualFilesMenuButtonVisible());
		assertTrue("There should be exactly one download individual files dropdown menu", page.downloadIndividualFilesMenuVisible());
		assertTrue("There should be a download button for the file that is displayed",
				page.downloadOutputFileButtonVisible());

		// Has no output files
		page = AnalysisDetailsPage.initPage(driver(), 10L, "output");
		assertTrue("Page title should equal", page.compareTabTitle("Output File Preview"));
		assertEquals("There should be no output files", 0, page.getNumberOfFilesDisplayed());
		assertEquals("Has a no output files alert", "No outputs available to display", page.getWarningAlertText());
	}

	@Test
	public void testPageSetUp() throws URISyntaxException, IOException {
		logger.debug("Testing 'Analysis Details Page'");

		LoginPage.loginAsManager(driver());
		// Submissions with trees and not sistr or biohansel
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 4L, "");
		assertTrue("Page title should equal", page.comparePageTitle("Tree Preview"));
		assertTrue("Has horizontal tab links", page.hasHorizontalTabLinks());

		// Completed submission should not display steps component
		assertTrue("Analysis steps are not visible since the analysis is in completed state",
				!page.analysisStepsVisible());

		// Submissions without trees and not sistr or biohansel
		page = AnalysisDetailsPage.initPage(driver(), 6L, "");
		assertTrue("Page title should equal", page.comparePageTitle("Output File Preview"));
		assertTrue("Has horizontal tab links", page.hasHorizontalTabLinks());

		// Any other submission state should display steps component
		page = AnalysisDetailsPage.initPage(driver(), 2L, "");
		assertTrue("Analysis steps are visible since the analysis isn't in completed state",
				page.analysisStepsVisible());
	}

	@Test
	// Has no specific results output tab (for example tree, biohansel, sistr) so the output file preview
	// page is the default view
	public void testPipelineResultsWithoutSpecialTab() throws IOException {
		fileUtilities.copyFileToDirectory(outputFileBaseDirectory,
				"src/test/resources/files/refseq-masher-matches.tsv");

		LoginPage.loginAsManager(driver());

		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 13L, "");
		assertTrue("Page title should equal", page.compareTabTitle("Output File Preview"));

		assertEquals("There should be one output file", 1, page.getNumberOfFilesDisplayed());
		assertTrue("There should be exactly one download all files button", page.downloadAllFilesButtonVisible());
		assertTrue("There should be a download button for the file that is displayed",
				page.downloadOutputFileButtonVisible());
	}

	@Test
	public void testProvenance() throws IOException {
		fileUtilities.copyFileToDirectory(outputFileBaseDirectory, "src/test/resources/files/filterStats.txt");
		LoginPage.loginAsManager(driver());

		// Has output files so display a provenance
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 4L, "provenance");
		assertTrue("Page title should equal", page.compareTabTitle("Provenance"));
		assertEquals("There should be two files", 2, page.getProvenanceFileCount());
		page.getFileProvenance(0);
		assertEquals("Should have 1 tool associated with filterStats", 1, page.getToolCount());
		page.displayToolExecutionParameters();
		assertEquals("Tool should have 2 parameters", 2, page.getGalaxyParametersCount());

		page.getFileProvenance(1);
		assertEquals("Should have 2 tools associated with the tree", 2, page.getToolCount());
		page.displayToolExecutionParameters();
		assertEquals("First tool should have 1 parameter", 1, page.getGalaxyParametersCount());

		// We click the first file again and check for tools and execution parameters
		page.getFileProvenance(0);
		assertEquals("Should have 1 tool associated with filterStats", 1, page.getToolCount());
		page.displayToolExecutionParameters();
		assertEquals("Tool should have 2 parameter", 2, page.getGalaxyParametersCount());

		// Has no output files so no provenance displayed
		page = AnalysisDetailsPage.initPage(driver(), 10L, "provenance");
		assertTrue("Page title should equal", page.compareTabTitle("Provenance"));
		assertEquals("There should be no file", 0, page.getProvenanceFileCount());
		assertEquals("Has a no provenance available alert",
				"Unable to display provenance as no output files were found for analysis.", page.getWarningAlertText());
	}

	@Test
	public void testSamplesPage() {
		LoginPage.loginAsManager(driver());
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 4L, "settings/samples");
		assertTrue("Page title should equal", page.compareTabTitle("Samples"));
		assertEquals("Should display 2 pairs of paired end files", 2, page.getNumberOfSamplesInAnalysis());

		// Filter samples by search string
		page.filterSamples("01-");
		assertEquals("Should display 1 pair of paired end files", 1, page.getNumberOfSamplesInAnalysis());

		// Download reference file button if file exists
		assertTrue("Should have a download reference file button", page.referenceFileDownloadButtonVisible());
	}

	@Test
	public void testSharedProjects() {
		LoginPage.loginAsManager(driver());
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 4L, "settings/share");
		assertTrue("Page title should equal", page.compareTabTitle("Manage Results"));
		assertTrue("Analysis shared projects", page.hasSharedWithProjects());

		page.removeSharedProjects();
		assertTrue("Analysis no longer shared with any projects", !page.hasSharedWithProjects());

		page.addSharedProjects();
		assertTrue("Analysis shared with projects", page.hasSharedWithProjects());
	}

	@Test
	public void testSistrOutput() throws IOException {
		fileUtilities.copyFileToDirectory(outputFileBaseDirectory,
				"src/test/resources/files/sistr-predictions-pass.json");

		LoginPage.loginAsManager(driver());

		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 11L, "");
		assertTrue("Page title should equal", page.comparePageTitle("SISTR Information"));
		assertTrue("Has vertical tabs for SISTR results and output files", page.hasSideBarTabLinks());

		assertTrue("Has 9 list items for SISTR Information", page.expectedNumberOfListItemsEqualsActual(9));

		page = AnalysisDetailsPage.initPage(driver(), 11L, "sistr/cgmlst");
		assertTrue("Page title should equal", page.comparePageTitle("cgMLST330"));
		assertTrue("Has 5 list items for cgMLST330", page.expectedNumberOfListItemsEqualsActual(5));

		page = AnalysisDetailsPage.initPage(driver(), 11L, "sistr/mash");
		assertTrue("Page title should equal", page.comparePageTitle("Mash"));
		assertTrue("Has 4 list items for Mash", page.expectedNumberOfListItemsEqualsActual(4));

		page = AnalysisDetailsPage.initPage(driver(), 11L, "sistr/citation");
		assertTrue("Page title should equal", page.comparePageTitle("Citation"));
		assertTrue("Page has a citation", page.citationVisible());

		page = AnalysisDetailsPage.initPage(driver(), 11L, "output");
		assertTrue("Page title should equal", page.comparePageTitle("Output File Preview"));
		assertEquals("There should be one output file", 1, page.getNumberOfFilesDisplayed());
		assertTrue("There should be exactly one download all files button", page.downloadAllFilesButtonVisible());
		assertTrue("There should be a download button for the file that is displayed",
				page.downloadOutputFileButtonVisible());
	}

	@Test
	// Successfully completed analysis (COMPLETED state)
	public void testTabRoutingAnalysisCompleted() throws URISyntaxException, IOException {
		LoginPage.loginAsManager(driver());

		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 4L, "");
		assertTrue("Page title should equal", page.comparePageTitle("Tree Preview"));

		page = AnalysisDetailsPage.initPage(driver(), 4L, "output");
		assertTrue("Page title should equal", page.comparePageTitle("Output File Preview"));

		page = AnalysisDetailsPage.initPage(driver(), 4L, "provenance");
		assertTrue("Page title should equal", page.comparePageTitle("Provenance"));

		page = AnalysisDetailsPage.initPage(driver(), 4L, "settings");
		assertTrue("Page title should equal", page.compareTabTitle("Details"));

		page = AnalysisDetailsPage.initPage(driver(), 4L, "settings/details");
		assertTrue("Page title should equal", page.compareTabTitle("Details"));

		page = AnalysisDetailsPage.initPage(driver(), 4L, "settings/samples");
		assertTrue("Page title should equal", page.compareTabTitle("Samples"));

		page = AnalysisDetailsPage.initPage(driver(), 4L, "settings/share");
		assertTrue("Page title should equal", page.compareTabTitle("Manage Results"));

		page = AnalysisDetailsPage.initPage(driver(), 4L, "settings/delete");
		assertTrue("Page title should equal", page.compareTabTitle("Delete Analysis"));
	}

	@Test
	//Analysis which did not complete successfully (ERROR State)
	public void testTabRoutingAnalysisError() throws URISyntaxException, IOException {
		LoginPage.loginAsManager(driver());
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 7L, "job-error");

		assertTrue("No job error information available alert visible", page.jobErrorAlertVisible());

		// Should not be able to view output files page if analysis errored
		page = AnalysisDetailsPage.initPage(driver(), 7L, "output");
		assertFalse("Page title should not equal", page.comparePageTitle("Output File Preview"));

		// Should not be able to view provenance page if analysis errored
		page = AnalysisDetailsPage.initPage(driver(), 7L, "provenance");
		assertFalse("Page title should not equal", page.comparePageTitle("Provenance"));

		page = AnalysisDetailsPage.initPage(driver(), 7L, "settings");
		assertTrue("Page title should equal", page.compareTabTitle("Details"));

		page = AnalysisDetailsPage.initPage(driver(), 7L, "settings/details");
		assertTrue("Page title should equal", page.compareTabTitle("Details"));

		page = AnalysisDetailsPage.initPage(driver(), 7L, "settings/samples");
		assertTrue("Page title should equal", page.compareTabTitle("Samples"));

		// Should not be able to share results if analysis errored
		page = AnalysisDetailsPage.initPage(driver(), 7L, "settings/share");
		assertFalse("Page title should not equal", page.compareTabTitle("Manage Results"));

		page = AnalysisDetailsPage.initPage(driver(), 7L, "settings/delete");
		assertTrue("Page title should equal", page.compareTabTitle("Delete Analysis"));
	}

	@Test
	public void testTreeOutput() {
		LoginPage.loginAsManager(driver());

		// Has tree file
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 4L, "");
		assertTrue("Page title should equal", page.comparePageTitle("Tree Preview"));

		assertTrue("Tree shape tools are visible", page.treeToolsVisible());
		assertTrue("Advanced Phylogenetic Tree button is visible", page.advancedPhylogeneticTreeButtonVisible());
		assertTrue("Tree wrapper is visible", page.phylocanvasWrapperVisible());
		assertTrue("Tree is visible", page.treeVisible());

		// Has no tree file
		page = AnalysisDetailsPage.initPage(driver(), 10L, "");
		assertTrue("Tree shape tools are not visible", page.treeToolsNotFound());
		assertTrue("Advanced Phylogenetic Tree button is not visible", page.advancedPhylogeneticTreeButtonNotFound());
		assertTrue("Tree wrapper is not visible", page.phylocanvasWrapperNotFound());
		assertTrue("Tree is not visible", page.treeNotFound());
		assertEquals("No outputs available to display", page.getWarningAlertText());
	}

	@Test
	public void testUnknownPipelineOutput() throws IOException, URISyntaxException, IridaWorkflowException {
		IridaWorkflowsService iridaWorkflowsService;
		IridaWorkflow unknownWorkflow;

		// Register an UNKNOWN workflow
		Path workflowVersion1DirectoryPath = Paths.get(TestAnalysis.class.getResource("workflows/TestAnalysis/1.0")
				.toURI());

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
		assertTrue("Page title should equal", page.compareTabTitle("Output File Preview"));

		assertEquals("There should be one output file", 1, page.getNumberOfFilesDisplayed());
		assertTrue("There should be exactly one download all files button", page.downloadAllFilesButtonVisible());
		assertTrue("There should be a download button for the file that is displayed",
				page.downloadOutputFileButtonVisible());

		page = AnalysisDetailsPage.initPage(driver(), 14L, "provenance");
		assertTrue("Page title should equal", page.compareTabTitle("Provenance"));
		assertEquals("There should be one file", 1, page.getProvenanceFileCount());
		page.getFileProvenance(0);
		assertEquals("Should have 2 tools associated with the tree", 1, page.getToolCount());
		page.displayToolExecutionParameters();
		assertEquals("First tool should have 2 parameter", 2, page.getGalaxyParametersCount());

		page = AnalysisDetailsPage.initPage(driver(), 14L, "settings/details");
		assertTrue("Page title should equal", page.compareTabTitle("Details"));
		assertEquals("There should be 7 labels for analysis details", 7, page.getNumberOfListItems());
		// Analysis Description doesn't have a value
		assertEquals("There should be only 6 values for these labels", 6, page.getNumberOfListItemValues());

		String expectedAnalysisDetails[] = new String[] { "My Completed Submission UNKNOWN PIPELINE", "14",
				"Unknown Pipeline (Unknown Version)", "MEDIUM", "Oct 6, 2013 10:01 AM", "a few seconds" };
		assertTrue("The correct details are displayed for the analysis",
				page.analysisDetailsEqual(expectedAnalysisDetails));
	}

	@Test
	public void testUpdateEmailPipelineResultVisibilty() throws URISyntaxException, IOException {
		LoginPage.loginAsManager(driver());
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 4L, "settings/details");
		assertTrue("Page title should equal", page.compareTabTitle("Details"));
		// As this analysis is in COMPLETED state the
		// Receive Email Upon Pipeline Completion section
		// should not be visible
		assertFalse("email pipeline result upon completion should be visible", page.emailPipelineResultVisible());

		page = AnalysisDetailsPage.initPage(driver(), 8L, "settings/details");
		assertTrue("Page title should equal", page.compareTabTitle("Details"));
		// As this analysis is not in COMPLETED state the
		// Receive Email Upon Pipeline Completion section
		// should be visible
		assertTrue("email pipeline result upon completion should be visible", page.emailPipelineResultVisible());
	}

}
