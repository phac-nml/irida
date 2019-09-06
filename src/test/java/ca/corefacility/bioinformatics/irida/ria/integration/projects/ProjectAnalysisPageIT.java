package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectAnalysesPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class ProjectAnalysisPageIT extends AbstractIridaUIITChromeDriver {
	private ProjectAnalysesPage projectAnalysesPage;

	@Test
	public void testGetProjectAnalyses() {
		LoginPage.loginAsManager(driver());
		projectAnalysesPage = ProjectAnalysesPage.initializeProjectAnalysesPage(driver(), 1);
		String foobar = "bax";
	}
}
