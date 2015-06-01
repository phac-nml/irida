package ca.corefacility.bioinformatics.irida.ria.integration.analysis;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIIT;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis.AnalysisDetailsPage;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/analysis/AnalysisAdminView.xml")
public class AnalysisDetailsPageIT extends AbstractIridaUIIT {
	private static final Logger logger = LoggerFactory.getLogger(AnalysisDetailsPageIT.class);

	@Override
	public WebDriver driverToUse() {
		return new ChromeDriver();
	}

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
		assertEquals("Should display 1 pair of paired end files", 2, page.getNumberOfPairedEndInputFiles());
	}
}
