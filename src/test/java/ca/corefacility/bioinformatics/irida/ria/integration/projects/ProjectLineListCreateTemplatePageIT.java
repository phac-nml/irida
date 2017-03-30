package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectLineListCreateTemplatePage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/ProjectLineListCreateTemplatePage.xml")
public class ProjectLineListCreateTemplatePageIT extends AbstractIridaUIITChromeDriver {
	private static final String SHORT_TEMPLATE_NAME = "aa";
	private static final String GOOD_TEMPLATE_NAME = "Unique Template Name";
	@Before
	public void init() {
		LoginPage.loginAsManager(driver());
	}

	@Test
	public void testPage() {
		// TODO: Implement after next merge request, there will be breaking changes.
	}
}
