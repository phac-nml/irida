package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.model.enums.ExportUploadState;
import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ExportDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.NcbiExportPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.Lists;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/NcbiExportPageIT.xml")
public class NcbiExportPageIT extends AbstractIridaUIITChromeDriver {

	private NcbiExportPage page;

	@BeforeEach
	public void setUp() {
		LoginPage.loginAsAdmin(driver());
	}

	@Test
	public void testCountSamples() {
		page = NcbiExportPage.goTo(driver(), 1L, Lists.newArrayList(1L, 2L, 4L));

		List<String> sampleNames = page.getSampleNames();
		assertEquals(2, sampleNames.size(), "should be 2 samples to submit");
		assertEquals(1, page.countDisabledSamples(), "should be 1 disabled sample");
	}

	@Test
	public void testSubmission() {
		page = NcbiExportPage.goTo(driver(), 1L, Lists.newArrayList(1L, 2L));

		page.fillTopLevelProperties("project", "NML", "NML");
		page.fillSamplesWithInfo("sample1", "protocol");

		page.submit();

		ExportDetailsPage exportPage = ExportDetailsPage.initPage(driver());

		assertEquals("Mr. Manager", exportPage.getSubmitter(), "Submitter should be set properly");
		assertEquals(ExportUploadState.NEW.toString(), exportPage.getUploadStatus(),
				"submission should be created with new status");
		assertEquals("project", exportPage.getBioproject(), "bioproject should be set properly");
		assertEquals("NML", exportPage.getOrganization(), "organization should be set properly");
		assertEquals(2, exportPage.getNumberOfSingles(), "should be 2 singles");
		assertEquals(0, exportPage.getNumberOfPairs(), "should be no pairs");
	}

	@Test
	public void testRemoveSubmission() {
		page = NcbiExportPage.goTo(driver(), 1L, Lists.newArrayList(1L, 2L));

		assertEquals(2, page.getSampleNames().size(), "should be 2 samples");

		page.removeFirstSample();

		assertEquals(1, page.getSampleNames().size(), "should be 1 sample after removal");
	}
}
