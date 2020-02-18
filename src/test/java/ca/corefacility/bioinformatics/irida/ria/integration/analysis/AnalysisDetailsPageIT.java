package ca.corefacility.bioinformatics.irida.ria.integration.analysis;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
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
import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis.AnalysesUserPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis.AnalysisDetailsPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/analysis/AnalysisAdminView.xml")
public class AnalysisDetailsPageIT extends AbstractIridaUIITChromeDriver {
	private static final Logger logger = LoggerFactory.getLogger(AnalysisDetailsPageIT.class);

	@Autowired
	@Qualifier("outputFileBaseDirectory")
	private Path outputFileBaseDirectory;

	@Before
	// Tree file used by multiple tests
	public void setFile() throws IOException {
		// We need to copy the file manually as it uses a relative path.
		final Path snpTree = Paths.get("src/test/resources/files/snp_tree.tree");
		try {
			Files.createDirectories(outputFileBaseDirectory.resolve(snpTree.getParent()));
		} catch (final FileAlreadyExistsException e) {
			logger.info("Directory already exists for snp tree.");
		}
		try {
			Files.copy(snpTree, outputFileBaseDirectory.resolve(snpTree));
		} catch (final FileAlreadyExistsException e) {
			logger.info("Already moved snp tree into directory.");
		}
	}

	@Test
	public void testAnalysisDetails() {
		LoginPage.loginAsManager(driver());
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 4L, "settings/details");
		assertTrue("Page title should equal", page.compareTabTitle("Details"));
		assertEquals("There should be 7 labels for analysis details", 7, page.getNumberOfListItems());
		// Analysis Description doesn't have a value
		assertEquals("There should be only 6 values for these labels", 6, page.getNumberOfListItemValues());

		assertTrue("The correct details are displayed for the analysis", page.analysisDetailsEqual());
	}

	@Test
	public void testBioHanselOutput() throws IOException {
		// We need to copy the file manually as it uses a relative path.
		final Path bioHanselResults = Paths.get("src/test/resources/files/bio_hansel-results.json");
		try {
			Files.createDirectories(outputFileBaseDirectory.resolve(bioHanselResults.getParent()));
		} catch (final FileAlreadyExistsException e) {
			logger.info("Directory already exists for bio_hansel results.");
		}
		try {
			Files.copy(bioHanselResults, outputFileBaseDirectory.resolve(bioHanselResults));
		} catch (final FileAlreadyExistsException e) {
			logger.info("Already moved bio_hansel results into directory.");
		}

		LoginPage.loginAsManager(driver());

		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 12L, "");
		assertTrue("Page title should equal", page.comparePageTitle("Bio Hansel Information"));
		assertTrue("Has vertical tabs for bio_hansel results and output files", page.hasSideBarTabLinks());

		assertTrue("Has 5 list items for Bio Hansel Information", page.expectedNumberOfListItemsEqualsActual(5));

		page = AnalysisDetailsPage.initPage(driver(), 12L, "biohansel/file_preview");
		assertTrue("Page title should equal", page.comparePageTitle("Output File Preview"));
		assertTrue("Page title should equal", page.compareTabTitle("Output File Preview"));
		assertEquals("There should be one output file", 1, page.getNumberOfFilesDisplayed());
		assertTrue("There should be exactly one download all files button", page.downloadAllFilesButtonVisible());
		assertTrue("There should be a download button for the file that is displayed", page.downloadOutputFileButtonVisible());
	}

	@Test
	public void testDeleteAnalysis() {
		LoginPage.loginAsManager(driver());

		AnalysesUserPage analysesPage = AnalysesUserPage.initializeAdminPage(driver());
		assertEquals("Should have 10 analyses displayed originally", 10, analysesPage.getNumberOfAnalysesDisplayed());

		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 9L, "settings/delete");
		assertTrue("Page title should equal", page.compareTabTitle("Delete Analysis"));
		assertTrue(page.deleteButtonExists());
		page.deleteAnalysis();

		analysesPage = AnalysesUserPage.initializeAdminPage(driver());
		// Still 10 analyses left after deleting as there is 13 in the db (10 displayed per page of table)
		assertEquals("Should have 10 analyses displayed", 10, analysesPage.getNumberOfAnalysesDisplayed());
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
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 4L, "tree/file_preview");
		assertTrue("Page title should equal", page.compareTabTitle("Output File Preview"));
		assertEquals("There should be one output file", 1, page.getNumberOfFilesDisplayed());
		assertTrue("There should be exactly one download all files button", page.downloadAllFilesButtonVisible());
		assertTrue("There should be a download button for the file that is displayed", page.downloadOutputFileButtonVisible());

		// Has no output files
		page = AnalysisDetailsPage.initPage(driver(), 10L, "tree/file_preview");
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
		assertTrue("Has sidebar tab links", page.hasSideBarTabLinks());
		assertTrue("Has horizontal tab links", page.hasHorizontalTabLinks());

		// Completed submission should not display steps component
		assertTrue("Analysis steps are not visible since the analysis is in completed state", !page.analysisStepsVisible());

		// Submissions without trees and not sistr or biohansel
		page = AnalysisDetailsPage.initPage(driver(), 6L, "");
		assertTrue("Page title should equal", page.comparePageTitle("Output File Preview"));
		assertTrue("Has horizontal tab links", page.hasHorizontalTabLinks());

		// Any other submission state should display steps component
		page = AnalysisDetailsPage.initPage(driver(), 2L, "");
		assertTrue("Analysis steps are visible since the analysis isn't in completed state", page.analysisStepsVisible());
	}

	@Test
	// Has no specific results output tab (for example tree, biohansel, sistr) so the output file preview
	// page is the default view
	public void testPipelineResultsWithoutSpecialTab() throws IOException{
		// We need to copy the file manually as it uses a relative path.
		final Path refSeqMashMatches = Paths.get("src/test/resources/files/refseq-masher-matches.tsv");
		try {
			Files.createDirectories(outputFileBaseDirectory.resolve(refSeqMashMatches.getParent()));
		} catch (final FileAlreadyExistsException e) {
			logger.info("Directory already exists for ref seq mash matches.");
		}
		try {
			Files.copy(refSeqMashMatches, outputFileBaseDirectory.resolve(refSeqMashMatches));
		} catch (final FileAlreadyExistsException e) {
			logger.info("Already moved ref seq mash matches into directory.");
		}

		LoginPage.loginAsManager(driver());

		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 13L, "");
		assertTrue("Page title should equal", page.compareTabTitle("Output File Preview"));

		assertEquals("There should be one output file", 1, page.getNumberOfFilesDisplayed());
		assertTrue("There should be exactly one download all files button", page.downloadAllFilesButtonVisible());
		assertTrue("There should be a download button for the file that is displayed", page.downloadOutputFileButtonVisible());
	}

	@Test
	public void testProvenance() {
		LoginPage.loginAsManager(driver());

		// Has output files so display a provenance
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 4L, "provenance");
		assertTrue("Page title should equal", page.compareTabTitle("Provenance"));
		assertEquals("There should be one file" , 1, page.getProvenanceFileCount());
		page.getFileProvenance();
		assertEquals("Should have 2 tools associated with the tree", 2, page.getToolCount());
		page.displayToolExecutionParameters();
		assertEquals("First tool should have 1 parameter", 1, page.getGalaxyParametersCount());

		// Has no output files so no provenance displayed
		page = AnalysisDetailsPage.initPage(driver(), 10L, "provenance");
		assertTrue("Page title should equal", page.compareTabTitle("Provenance"));
		assertEquals("There should be no file" , 0, page.getProvenanceFileCount());
		assertEquals("Has a no provenance available alert", "Unable to display provenance as no output files were found for analysis.", page.getWarningAlertText());
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
		// We need to copy the file manually as it uses a relative path.
		final Path sistrPredictions = Paths.get("src/test/resources/files/sistr-predictions-pass.json");
		try {
			Files.createDirectories(outputFileBaseDirectory.resolve(sistrPredictions.getParent()));
		} catch (final FileAlreadyExistsException e) {
			logger.info("Directory already exists for sistr predictions.");
		}
		try {
			Files.copy(sistrPredictions, outputFileBaseDirectory.resolve(sistrPredictions));
		} catch (final FileAlreadyExistsException e) {
			logger.info("Already moved sistr predictions into directory.");
		}

		LoginPage.loginAsManager(driver());

		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 11L, "");
		assertTrue("Page title should equal", page.comparePageTitle("SISTR Information"));
		assertTrue("Has vertical tabs for SISTR results and output files", page.hasSideBarTabLinks());

		assertTrue("Has 2 list items for SISTR Information", page.expectedNumberOfListItemsEqualsActual(2));

		page = AnalysisDetailsPage.initPage(driver(), 11L, "sistr/serovar_predictions");
		assertTrue("Page title should equal", page.comparePageTitle("Serovar Predictions"));
		assertTrue("Has 7 list items for Serovar Predictions", page.expectedNumberOfListItemsEqualsActual(7));

		page = AnalysisDetailsPage.initPage(driver(), 11L, "sistr/cgmlst");
		assertTrue("Page title should equal", page.comparePageTitle("cgMLST330"));
		assertTrue("Has 5 list items for cgMLST330", page.expectedNumberOfListItemsEqualsActual(5));

		page = AnalysisDetailsPage.initPage(driver(), 11L, "sistr/mash");
		assertTrue("Page title should equal", page.comparePageTitle("Mash"));
		assertTrue("Has 4 list items for Mash",  page.expectedNumberOfListItemsEqualsActual(4));

		page = AnalysisDetailsPage.initPage(driver(), 11L, "sistr/file_preview");
		assertTrue("Page title should equal", page.comparePageTitle("Output File Preview"));
		assertEquals("There should be one output file", 1, page.getNumberOfFilesDisplayed());
		assertTrue("There should be exactly one download all files button", page.downloadAllFilesButtonVisible());
		assertTrue("There should be a download button for the file that is displayed", page.downloadOutputFileButtonVisible());

		page = AnalysisDetailsPage.initPage(driver(), 11L, "sistr/citation");
		assertTrue("Page title should equal", page.comparePageTitle("Citation"));
		assertTrue("Page has a citation", page.citationVisible());
	}

	@Test
	// Successfully completed analysis (COMPLETED state)
	public void testTabRoutingAnalysisCompleted() throws URISyntaxException, IOException {
		LoginPage.loginAsManager(driver());

		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 4L, "");
		assertTrue("Page title should equal", page.comparePageTitle("Tree Preview"));

		page = AnalysisDetailsPage.initPage(driver(), 4L, "tree/file_preview");
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
		assertEquals("Unable to find tree file. No tree available to display.", page.getWarningAlertText());
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

	@Test
	public void testUnknownPipelineOutput() {
		LoginPage.loginAsManager(driver());
		// Has an UNKNOWN analysis type
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 14L, "");


		assertTrue("Page title should equal", page.compareTabTitle("Output File Preview"));

		assertEquals("There should be one output file", 1, page.getNumberOfFilesDisplayed());
		assertTrue("There should be exactly one download all files button", page.downloadAllFilesButtonVisible());
		assertTrue("There should be a download button for the file that is displayed", page.downloadOutputFileButtonVisible());
	}
}
