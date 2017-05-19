package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.Before;
import org.junit.Test;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/ProjectLineListCreateTemplatePage.xml")
public class ProjectLineListCreateTemplatePageIT extends AbstractIridaUIITChromeDriver {
	@Before
	public void init() {
		LoginPage.loginAsManager(driver());
	}

	@Test
	public void testPage() {
		// TODO: Implement after next merge request, there will be breaking
		// changes.
	}
}
