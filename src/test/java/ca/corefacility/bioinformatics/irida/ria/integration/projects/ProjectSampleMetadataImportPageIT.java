package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSampleMetadataImportPage;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/ProjectSampleMetadataView.xml")
public class ProjectSampleMetadataImportPageIT extends AbstractIridaUIITChromeDriver {
	private static final String GOOD_FILE_PATH = "src/test/resources/files/metadata-upload/good.xlsx";
	private static final String MIXED_FILE_PATH = "src/test/resources/files/metadata-upload/mixed.xlsx";

	@Before
	public void init() {
		LoginPage.loginAsManager(driver());
	}

	@Test
	public void testGoodFileAndHeaders() {
		ProjectSampleMetadataImportPage page = ProjectSampleMetadataImportPage.goToPage(driver());
		page.uploadMetadataFile(GOOD_FILE_PATH);
		page.selectSampleNameColumn();
		assertEquals("Has incorrect amount of rows matching sample names", 5, page.getFoundCount());
		assertEquals("Has incorrect amout of rows missing sample names", 0, page.getMissingCount());
	}

	@Test
	public void testMixedFileAndHeaders() {
		ProjectSampleMetadataImportPage page = ProjectSampleMetadataImportPage.goToPage(driver());
		page.uploadMetadataFile(MIXED_FILE_PATH);
		page.selectSampleNameColumn();
		assertEquals("Has incorrect amount of rows matching sample names", 5, page.getFoundCount());
		assertEquals("Has incorrect amout of rows missing sample names", 2, page.getMissingCount());
	}
}
