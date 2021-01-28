package ca.corefacility.bioinformatics.irida.ria.integration.pipelines;

import java.io.IOException;

import org.junit.Before;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.pipelines.LaunchPipelinePage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSamplesPage;

public class BasePipelineLaunchPageIT extends AbstractIridaUIITChromeDriver {
	protected LaunchPipelinePage page;

	@Before
	public void setUpTest() throws IOException {
		page = LaunchPipelinePage.init(driver());
		addSamplesToCart();
	}

	private void addSamplesToCart() {
		LoginPage.loginAsUser(driver());
		ProjectSamplesPage samplesPage = ProjectSamplesPage.gotToPage(driver(), 1);
		samplesPage.selectSample(0);
		samplesPage.selectSample(1);
		samplesPage.addSelectedSamplesToCart();
	}
}
