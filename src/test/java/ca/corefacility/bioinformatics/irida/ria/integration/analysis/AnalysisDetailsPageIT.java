package ca.corefacility.bioinformatics.irida.ria.integration.analysis;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;

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

	@Test
	public void testPageSetUp() throws URISyntaxException, IOException {
		logger.debug("Testing 'Analysis Details Page'");

		LoginPage.loginAsManager(driver());
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 4L);

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
	public void testEditPriorityHidden() throws URISyntaxException, IOException {

		LoginPage.loginAsManager(driver());
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver(), 4L);

		page.clickEditButton();

		assertFalse("priority edit should be hidden", page.priorityEditVisible());

		page = AnalysisDetailsPage.initPage(driver(), 8L);

		assertTrue("priority edit should be visible", page.priorityEditVisible());
	}

}
