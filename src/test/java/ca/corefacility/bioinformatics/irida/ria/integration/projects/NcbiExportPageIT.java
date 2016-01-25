package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.NcbiExportPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.Lists;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/NcbiExportPageIT.xml")
public class NcbiExportPageIT extends AbstractIridaUIITChromeDriver {

	private NcbiExportPage page;

	@Test
	public void testCountSamples() {
		LoginPage.loginAsAdmin(driver());

		page = NcbiExportPage.goTo(driver(), 1L, Lists.newArrayList(1L, 2L, 4L));

		List<String> sampleNames = page.getSampleNames();
		assertEquals(2, sampleNames.size());
		assertEquals(1, page.countDisabledSamples());
	}
}
