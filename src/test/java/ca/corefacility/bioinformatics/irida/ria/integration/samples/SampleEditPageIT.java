package ca.corefacility.bioinformatics.irida.ria.integration.samples;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITPhantomJS;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.samples.SampleDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.samples.SampleEditPage;
import ca.corefacility.bioinformatics.irida.ria.web.samples.SamplesController;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * <p> Integration test to ensure that the Sample Details Page. </p>
 *
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/samples/SamplePagesIT.xml")
public class SampleEditPageIT extends AbstractIridaUIITPhantomJS {
	private SampleEditPage page;
	private SampleDetailsPage detailsPage;

	@Before
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
		assertTrue("User should be redirected to the details page",
				driver().getCurrentUrl().contains(SampleDetailsPage.RELATIVE_URL));
		assertEquals("Ensure that the organism has been updated", organismName, detailsPage.getOrganismName());
	}

	@Test
	public void testBadFormSubmission() {
		String badLatitude = "not a latitude";
		String goodLatitude = "23.44443";
		page.setFieldValue(SamplesController.LATITUDE, badLatitude);
		page.submitForm();
		assertTrue("Should be redirected to the form", driver().getCurrentUrl().contains(SampleEditPage.RELATIVE_URL));
		assertTrue("Should have an error field on the latitude",
				page.isErrorLabelDisplayedForField(SamplesController.LATITUDE));
		assertFalse("No other field should have an error",
				page.isErrorLabelDisplayedForField(SamplesController.ORGANISM));
		assertFalse("No other field should have an error",
				page.isErrorLabelDisplayedForField(SamplesController.LONGITUDE));
		assertFalse("No other field should have an error",
				page.isErrorLabelDisplayedForField(SamplesController.ISOLATE));
		// Submit a good latitude
		page.setFieldValue(SamplesController.LATITUDE, goodLatitude);
		page.submitForm();
		assertTrue("User should be redirected to the details page",
				driver().getCurrentUrl().contains(SampleDetailsPage.RELATIVE_URL));
	}
}
