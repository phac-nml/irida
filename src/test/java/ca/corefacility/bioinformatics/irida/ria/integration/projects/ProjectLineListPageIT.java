package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectLineListPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;

/**
 * <p>
 * Integration test to ensure that the Project Line List Page is working.
 * </p>
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/ProjectLineListView.xml")
@UsingDataSet(locations = "/ca/corefacility/bioinformatics/irida/ria/web/projects/LineListView.json",
			  loadStrategy = LoadStrategyEnum.INSERT)
public class ProjectLineListPageIT extends AbstractIridaUIITChromeDriver {

	@Before
	public void init() {
		LoginPage.loginAsManager(driver());
	}

	@Test
	public void testTableSetup() {
		ProjectLineListPage page = ProjectLineListPage.goToPage(driver(), 1);
		assertEquals("Should be on the correct page.", "Line List", page.getActivePage());
		assertEquals("Should be 3 samples with metadata", 3, page.getNumberSamplesWithMetadata());
	}
}
