package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.model.enums.ExportUploadState;
import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ExportDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.NcbiExportPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.Lists;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/NcbiExportPageIT.xml")
public class NcbiExportPageIT extends AbstractIridaUIITChromeDriver {

	private NcbiExportPage page;

	@Before
	public void setUp() {
		LoginPage.loginAsAdmin(driver());
	}

	@Test
	public void testCountSamples() {
		page = NcbiExportPage.goTo(driver(), 1L, Lists.newArrayList(1L, 2L, 4L));

		List<String> sampleNames = page.getSampleNames();
		assertEquals("should be 2 samples to submit", 2, sampleNames.size());
		assertEquals("should be 1 disabled sample", 1, page.countDisabledSamples());
	}

	@Test
	public void testSubmission() {
		page = NcbiExportPage.goTo(driver(), 1L, Lists.newArrayList(1L, 2L));

		page.fillTopLevelProperties("project", "NML", "NML");
		page.fillSamplesWithInfo("sample1", "protocol");

		page.submit();

		ExportDetailsPage exportPage = ExportDetailsPage.initPage(driver());

		assertEquals("submission should be created with new status", ExportUploadState.NEW.toString(),
				exportPage.getUploadStatus());
	}

	@Test
	public void testRemoveSubmission() {
		page = NcbiExportPage.goTo(driver(), 1L, Lists.newArrayList(1L, 2L));

		assertEquals("should be 2 samples", 2, page.getSampleNames().size());

		page.removeFirstSample();

		assertEquals("should be 1 sample after removal", 1, page.getSampleNames().size());
	}
}
