package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectLineListPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSampleMetadataImportPage;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/ProjectSampleMetadataView.xml")
public class ProjectLineListPageIT extends AbstractIridaUIITChromeDriver {
	private static final String GOOD_FILE_PATH = "src/test/resources/files/metadata-upload/good.xlsx";

	@Before
	public void init() {
		LoginPage.loginAsManager(driver());

		// Upload some metadata
		ProjectSampleMetadataImportPage page = ProjectSampleMetadataImportPage.goToPage(driver());
		page.uploadMetadataFile(GOOD_FILE_PATH);
	}

	@Test
	public void testDefaultTable() {
		ProjectLineListPage page = ProjectLineListPage.goToPage(driver());
		// Should have the default project view displayed
		assertEquals("Not displaying the default template", 23, page.getTableHeaderCount());
	}
}
