package ca.corefacility.bioinformatics.irida.ria.integration.samples;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.samples.SampleDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.samples.SampleEditPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.TestUtilities;
import ca.corefacility.bioinformatics.irida.ria.web.samples.SamplesController;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

/**
 * <p> Integration test to ensure that the Sample Details Page. </p>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/samples/SamplePagesIT.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class SampleEditPageIT {
	private WebDriver driver;
	private SampleEditPage page;
	private SampleDetailsPage detailsPage;

	@Before
	public void setUp() {
		driver = TestUtilities.setDriverDefaults(new PhantomJSDriver());
		LoginPage.loginAsManager(driver);
		page = new SampleEditPage(driver);
		detailsPage = new SampleDetailsPage(driver);
		page.goToPage();
	}

	@After
	public void destroy() {
		driver.quit();
	}

	@Test
	public void testProperFormSubmission() {
		String organismName = "E .coli";
		page.setFieldValue(SamplesController.ORGANISM, organismName);
		page.submitForm();
		assertTrue("User should be redirected to the details page",
				driver.getCurrentUrl().contains(SampleDetailsPage.RELATIVE_URL));
		assertEquals("Ensure that the organism has been updated", organismName, detailsPage.getOrganismName());
	}

	@Test
	public void testBadFormSubmission() {
		String badLatitude = "not a latitude";
		String goodLatitude = "23.44443";
		page.setFieldValue(SamplesController.LATITUDE, badLatitude);
		page.submitForm();
		assertTrue("Should be redirected to the form", driver.getCurrentUrl().contains(SampleEditPage.RELATIVE_URL));
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
				driver.getCurrentUrl().contains(SampleDetailsPage.RELATIVE_URL));
	}
}
