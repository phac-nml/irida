package ca.corefacility.bioinformatics.irida.ria.integration.analysis;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis.AnalysisDetailsPage;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/analysis/AnalysisAdminView.xml")
public class AnalysisDetailsPageIT extends AbstractIridaUIITChromeDriver {
	private static final Logger logger = LoggerFactory.getLogger(AnalysisDetailsPageIT.class);

	@Ignore
	@Test
	public void testPageSetUp() throws URISyntaxException, IOException {
		logger.debug("Testing 'Analysis Details Page'");

		LoginPage.loginAsManager(driver());
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 4L, "");

		// Ensure files are displayed
		page.displayProvenanceView();
		assertEquals("Should be displaying 1 file", 1, page.getNumberOfFilesDisplayed());

		// Ensure tools are displayed
		page.displayTreeTools();
		assertEquals("Should have 2 tools associated with the tree", 2, page.getNumberOfToolsForTree());

		// Ensure the tool parameters can be displayed;
		assertEquals("First tool should have 1 parameter", 1, page.getNumberOfParametersForTool());

		// Ensure the input files are displayed
		// 2 Files expected since they are a pair.
		page.displayInputFilesTab();
		assertEquals("Should display 2 pairs of paired end files", 2, page.getNumberOfSamplesInAnalysis());
		assertEquals("Sample 1 should not be related to a sample", "Unknown Sample", page.getLabelForSample(0));
	}

	@Test
	public void testEditPriorityVisibility() throws URISyntaxException, IOException {
		LoginPage.loginAsManager(driver());
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 4L, "details");
		assertTrue("Page title should equal", page.comparePageTitle("Details"));

		// As this analysis is not in NEW state the
		// edit priority dropdown should not be visible
		assertFalse("priority edit should be visible", page.priorityEditVisible());

		page = AnalysisDetailsPage.initPage(driver(), 8L, "details");
		assertTrue("Page title should equal", page.comparePageTitle("Details"));
		// As this analysis is in NEW state the
		// edit priority dropdown should be visible
		assertTrue("priority edit should be visible", page.priorityEditVisible());
	}

	@Test
	public void testUpdateEmailPipelineResultVisibilty() throws URISyntaxException, IOException {
		LoginPage.loginAsManager(driver());
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 4L, "details");
		assertTrue("Page title should equal", page.comparePageTitle("Details"));
		// As this analysis is in COMPLETED state the
		// Receive Email Upon Pipeline Completion section
		// should not be visible
		assertFalse("email pipeline result upon completion should be visible", page.emailPipelineResultVisible());

		page = AnalysisDetailsPage.initPage(driver(), 8L, "details");
		assertTrue("Page title should equal", page.comparePageTitle("Details"));
		// As this analysis is not in COMPLETED state the
		// Receive Email Upon Pipeline Completion section
		// should be visible
		assertTrue("email pipeline result upon completion should be visible", page.emailPipelineResultVisible());
	}

	@Test
	// Successfullu completed analysis (COMPLETED state)
	public void testTabRoutingAnalysisCompleted() throws URISyntaxException, IOException {
		LoginPage.loginAsManager(driver());

		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 6L, "output-files");
		assertTrue("Page title should equal", page.comparePageTitle("Output Files"));

		page = AnalysisDetailsPage.initPage(driver(), 6L, "provenance");
		assertTrue("Page title should equal", page.comparePageTitle("Provenance"));

		page = AnalysisDetailsPage.initPage(driver(), 6L, "settings");
		assertTrue("Page title should equal", page.comparePageTitle("Details"));

		page = AnalysisDetailsPage.initPage(driver(), 6L, "details");
		assertTrue("Page title should equal", page.comparePageTitle("Details"));

		page = AnalysisDetailsPage.initPage(driver(), 6L, "samples");
		assertTrue("Page title should equal", page.comparePageTitle("Samples"));

		page = AnalysisDetailsPage.initPage(driver(), 6L, "share");
		assertTrue("Page title should equal", page.comparePageTitle("Manage Results"));

		page = AnalysisDetailsPage.initPage(driver(), 6L, "delete");
		assertTrue("Page title should equal", page.comparePageTitle("Delete Analysis"));

		page = AnalysisDetailsPage.initPage(driver(), 6L, "job-error");
		assertFalse("Page title should not equal", page.comparePageTitle("Job Error"));
	}

	@Test
	//Analysis which did not complete successfully (ERROR State)
	public void testTabRoutingAnalysisError() throws URISyntaxException, IOException {
		LoginPage.loginAsManager(driver());
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 7L, "job-error");
		assertTrue("Page title should equal", page.comparePageTitle("Job Error"));

		page = AnalysisDetailsPage.initPage(driver(), 7L, "output-files");
		assertFalse("Page title should not equal", page.comparePageTitle("Output Files"));

		page = AnalysisDetailsPage.initPage(driver(), 7L, "provenance");
		assertFalse("Page title should not equal", page.comparePageTitle("Provenance"));

		page = AnalysisDetailsPage.initPage(driver(), 7L, "settings");
		assertTrue("Page title should equal", page.comparePageTitle("Details"));

		page = AnalysisDetailsPage.initPage(driver(), 7L, "details");
		assertTrue("Page title should equal", page.comparePageTitle("Details"));

		page = AnalysisDetailsPage.initPage(driver(), 7L, "samples");
		assertTrue("Page title should equal", page.comparePageTitle("Samples"));

		page = AnalysisDetailsPage.initPage(driver(), 7L, "share");
		assertFalse("Page title should not equal", page.comparePageTitle("Manage Results"));

		page = AnalysisDetailsPage.initPage(driver(), 7L, "delete");
		assertTrue("Page title should equal", page.comparePageTitle("Delete Analysis"));
	}

}
