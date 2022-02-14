package ca.corefacility.bioinformatics.irida.ria.integration.samples;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.samples.SampleDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.samples.SampleEditPage;
import ca.corefacility.bioinformatics.irida.ria.web.samples.SamplesController;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p> Integration test to ensure that the Sample Details Page. </p>
 *
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/samples/SamplePagesIT.xml")
public class SampleEditPageIT extends AbstractIridaUIITChromeDriver {
	private SampleEditPage page;
	private SampleDetailsPage detailsPage;

	@BeforeEach
	public void setUpTest() {
		LoginPage.loginAsManager(driver());
		page = new SampleEditPage(driver());
		detailsPage = new SampleDetailsPage(driver());
		page.goToPage();
	}

	@Test
	public void testProperFormSubmission() {
		String organismName = "E .coli";
		page.setFieldValue(SamplesController.ORGANISM, organismName);
		page.submitForm();
		assertTrue(driver().getCurrentUrl().contains(SampleDetailsPage.RELATIVE_URL),
				"User should be redirected to the details page");
		assertEquals(organismName, detailsPage.getOrganismName(), "Ensure that the organism has been updated");
	}

	@Test
	public void testBadFormSubmission() {
		String badLatitude = "not a latitude";
		String goodLatitude = "23.44443";
		page.setFieldValue(SamplesController.LATITUDE, badLatitude);
		page.submitForm();
		assertTrue(driver().getCurrentUrl().contains(SampleEditPage.RELATIVE_URL), "Should be redirected to the form");
		assertTrue(page.isErrorLabelDisplayedForField(SamplesController.LATITUDE),
				"Should have an error field on the latitude");
		assertFalse(page.isErrorLabelDisplayedForField(SamplesController.ORGANISM),
				"No other field should have an error");
		assertFalse(page.isErrorLabelDisplayedForField(SamplesController.LONGITUDE),
				"No other field should have an error");
		assertFalse(page.isErrorLabelDisplayedForField(SamplesController.ISOLATE),
				"No other field should have an error");
		// Submit a good latitude
		page.setFieldValue(SamplesController.LATITUDE, goodLatitude);
		page.submitForm();
		assertTrue(driver().getCurrentUrl().contains(SampleDetailsPage.RELATIVE_URL),
				"User should be redirected to the details page");
	}
}
