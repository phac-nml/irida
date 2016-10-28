package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis.AnalysesUserPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis.AnalysisDetailsPage;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class ProjectAnalysisPageIT extends AbstractIridaUIITChromeDriver {
	AnalysesUserPage page;

	public void setUp() {
		LoginPage.loginAsManager(driver());
	}

	@Test
	public void testGetProjectAnalyses() {
		Long projectId = 1L;
		page = AnalysesUserPage.initializeProjectPage(projectId, driver());

		assertEquals("should be 2 analyses", 2, page.getNumberOfAnalyses());
		
		AnalysisDetailsPage analysisPage = AnalysisDetailsPage.initPage(driver(), 1L);
		
		analysisPage.displayInputFilesTab();
		
		List<Long> sharedProjectIds = analysisPage.getSharedProjectIds();
		assertEquals("should be 1 shared project", 1, sharedProjectIds.size());
		Long sharedId = sharedProjectIds.iterator().next();
		
		assertEquals("should be shared with project 1", new Long(1), sharedId);
		
	}
}
