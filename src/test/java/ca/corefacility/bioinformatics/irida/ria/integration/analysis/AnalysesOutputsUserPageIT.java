package ca.corefacility.bioinformatics.irida.ria.integration.analysis;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis.AnalysesUserOutputsPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.ImmutableList;

import static org.junit.Assert.assertEquals;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/analysis/AnalysisAdminView.xml")
public class AnalysesOutputsUserPageIT extends AbstractIridaUIITChromeDriver {
	private AnalysesUserOutputsPage userAnalysesOutputsPage;

	@Test
	public void testGetUserSingleSampleAnalysisOutputs() {
		LoginPage.loginAsManager(driver());
		userAnalysesOutputsPage = AnalysesUserOutputsPage.initializeAnalysesUserSingleSampleAnalysisOutputsPage(driver());

		checkTranslations(userAnalysesOutputsPage, ImmutableList.of("analyses-outputs"), null);
		assertEquals("Should have 4 single sample analysis outputs displayed", 4, userAnalysesOutputsPage.getNumberSingleSampleAnalysisOutputsDisplayed());

		userAnalysesOutputsPage.searchOutputs("sistr");
		assertEquals("Should have 1 single sample analysis outputs displayed after filtering", 1, userAnalysesOutputsPage.getNumberSingleSampleAnalysisOutputsDisplayed());

		userAnalysesOutputsPage.clearSearchOutputs();
		assertEquals("Should have 4 single sample analysis outputs displayed after removing filtering", 4, userAnalysesOutputsPage.getNumberSingleSampleAnalysisOutputsDisplayed());
	}
}
