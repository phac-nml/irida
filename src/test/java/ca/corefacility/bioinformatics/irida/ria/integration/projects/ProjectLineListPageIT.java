package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectLineListPage;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/ProjectSampleMetadataView.xml")
public class ProjectLineListPageIT extends AbstractIridaUIITChromeDriver {

	@Before
	public void init() {
		LoginPage.loginAsManager(driver());
	}

	@Test
	public void testDefaultTable() {
		ProjectLineListPage page = ProjectLineListPage.goToPage(driver());
		// Should have the default project view displayed
	}
}
