package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis.AnalysesUserPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class ProjectAnalysisPageIT extends AbstractIridaUIITChromeDriver {
	AnalysesUserPage page;

	@Before
	public void setUp() {
		
	}

	@Test
	public void testGetProjectAnalyses() {
		LoginPage.loginAsManager(driver());
		Long projectId = 1L;
	}
}
