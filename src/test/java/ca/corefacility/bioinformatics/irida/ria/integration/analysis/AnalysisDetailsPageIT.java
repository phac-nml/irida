package ca.corefacility.bioinformatics.irida.ria.integration.analysis;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis.AnalysesUserPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis.AnalysisDetailsPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.*;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/analysis/AnalysisAdminView.xml")
@Ignore
public class AnalysisDetailsPageIT extends AbstractIridaUIITChromeDriver {
	private static final Logger logger = LoggerFactory.getLogger(AnalysisDetailsPageIT.class);


	@Test
	public void testPageSetUp() throws URISyntaxException, IOException {
		logger.debug("Testing 'Analysis Details Page'");

		LoginPage.loginAsManager(driver());
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 4L, "");
		assertTrue("Page title should equal", page.comparePageTitle("Tree Preview"));

		assertTrue("Has sidebar tab links", page.hasSideBarTabLinks());
		assertTrue("Has horizontal tab links", page.hasHorizontalTabLinks());
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
	// Successfully completed analysis (COMPLETED state)
	public void testTabRoutingAnalysisCompletedTree() throws URISyntaxException, IOException {
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
	public void testSamplesPage() {
		LoginPage.loginAsManager(driver());
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 4L, "settings/samples");
		assertTrue("Page title should equal", page.compareTabTitle("Samples"));
		assertEquals("Should display 2 pairs of paired end files", 2, page.getNumberOfSamplesInAnalysis());
	}

	@Test
	public void testDeleteAnalysis() {
		LoginPage.loginAsManager(driver());

		AnalysesUserPage analysesPage = AnalysesUserPage.initializeAdminPage(driver());
		assertEquals("Should have 9 analyses displayed originally", 9, analysesPage.getNumberOfAnalysesDisplayed());

		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 9L, "settings/delete");
		assertTrue("Page title should equal", page.compareTabTitle("Delete Analysis"));
		assertTrue(page.deleteButtonExists());
		page.deleteAnalysis();

		analysesPage = AnalysesUserPage.initializeAdminPage(driver());
		assertEquals("Should have 8 analyses left", 8, analysesPage.getNumberOfAnalysesDisplayed());
	}

	@Test
	public void testSharedProjects() {
		LoginPage.loginAsManager(driver());
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 4L, "settings/share");
		assertTrue("Page title should equal", page.compareTabTitle("Manage Results"));
		assertTrue("Analysis shared projects", page.hasSharedWithProjects());

		page.removeSharedProjects();
		assertTrue("Analysis no longer shared with any projects", !page.hasSharedWithProjects());
	}

	@Test
	public void testAnalysisDetails() {
		LoginPage.loginAsManager(driver());
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 4L, "settings/details");
		assertTrue("Page title should equal", page.compareTabTitle("Details"));
		assertEquals("There should be 7 labels for analysis details", 7, page.getNumberOfListItems());

		// Analysis Description doesn't have a value
		assertEquals("There should be only 6 values for these labels", 6, page.getNumberOfListItemValues());
	}

	@Test
	public void testProvenance() {
		LoginPage.loginAsManager(driver());
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 4L, "provenance");

		assertTrue("Page title should equal", page.compareTabTitle("Provenance"));

		assertEquals("There should be one file" , 1, page.getProvenanceFileCount());
	}

	@Ignore
	@Test
	public void testOutputFiles() {
		LoginPage.loginAsManager(driver());
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 4L, "output");
		assertTrue("Page title should equal", page.compareTabTitle("Output File Preview"));

		assertEquals("There should be one output file", 1, page.getNumberOfFilesDisplayed());
	}
}
