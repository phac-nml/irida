package ca.corefacility.bioinformatics.irida.ria.integration.analysis;

import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis.AnalysesUserOutputsPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.ImmutableList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/analysis/AnalysisAdminView.xml")
public class AnalysesOutputsUserPageIT extends AbstractIridaUIITChromeDriver {
	private AnalysesUserOutputsPage userAnalysesOutputsPage;

	@Test
	public void testGetUserSingleSampleAnalysisOutputs() {
		LoginPage.loginAsManager(driver());
		userAnalysesOutputsPage = AnalysesUserOutputsPage.initializeAnalysesUserSingleSampleAnalysisOutputsPage(driver());

		checkTranslations(userAnalysesOutputsPage, ImmutableList.of("analyses-outputs"), null);
		assertEquals(4, userAnalysesOutputsPage.getNumberSingleSampleAnalysisOutputsDisplayed(), "Should have 4 single sample analysis outputs displayed");

		userAnalysesOutputsPage.searchOutputs("sistr");
		assertEquals(1, userAnalysesOutputsPage.getNumberSingleSampleAnalysisOutputsDisplayed(), "Should have 1 single sample analysis outputs displayed after filtering");

		userAnalysesOutputsPage.clearSearchOutputs();
		assertEquals(4, userAnalysesOutputsPage.getNumberSingleSampleAnalysisOutputsDisplayed(), "Should have 4 single sample analysis outputs displayed after removing filtering");
	}
}
