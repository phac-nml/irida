package ca.corefacility.bioinformatics.irida.ria.integration.pipelines;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.*;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.pipelines.PipelinesPhylogenomicsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.pipelines.PipelinesSelectionPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSamplesPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.TestUtilities;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

/**
 * <p>
 * Testing for launching a phylogenomics pipeline.
 * </p>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/pipelines/PipelinePhylogenomicsView.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class PipelinesPhylogenomicsPageIT {
	private static final Logger logger = LoggerFactory.getLogger(PipelinesPhylogenomicsPageIT.class);
	private static WebDriver driver;
	private PipelinesPhylogenomicsPage page;

	@BeforeClass
	public static void setUp() throws IOException, URISyntaxException {
		driver = TestUtilities.setDriverDefaults(new ChromeDriver());
	}

	@Before
	public void setUpTest() {
		page = new PipelinesPhylogenomicsPage(driver);
	}

	@After
	public void tearDown() {
		LoginPage.logout(driver);
	}

	@AfterClass
	public static void destroy() {
		driver.quit();
	}

	@Test
	public void testPageSetup() {
		addSamplesToCart();

		logger.info("Checking Phylogenomics Page Setup.");
		assertEquals("Should display the correct number of reference files in the select input.", 2,
				page.getReferenceFileCount());
		assertEquals("Should display the correct number of samples.", 2, page.getNumberOfSamplesDisplayed());
	}

	@Test
	public void testNoRefFileNoPermissions() {
		LoginPage.loginAsUser(driver);

		// Add sample from a project that user is a "Project User" and has no
		// reference files.
		ProjectSamplesPage samplesPage = new ProjectSamplesPage(driver);
		samplesPage.goToPage("2");
		samplesPage.selectSampleByRow(1);
		samplesPage.addSamplesToGlobalCart();

		PipelinesSelectionPage.goToPhylogenomicsPipeline(driver);
		assertTrue("Should display a warning to the user that there are no reference files.",
				page.isNoReferenceWarningDisplayed());
		assertTrue(
				"Should display a message saying that the user cannot upload reference files to their selected projects.",
				page.isNoRightsMessageDisplayed());
		assertFalse("Should show the user which projects they can upload files to.",
				page.isAddReferenceFileLinksDisplayed());
		assertFalse("Should not be able to create a pipeline", page.isCreatePipelineAreaVisible());
	}

	@Test
	public void testNoRefFileWithPermissions() {
		LoginPage.loginAsManager(driver);
		ProjectSamplesPage samplesPage = new ProjectSamplesPage(driver);
		samplesPage.goToPage("2");
		samplesPage.selectSampleByRow(1);
		samplesPage.selectSampleByRow(2);
		samplesPage.addSamplesToGlobalCart();
		PipelinesSelectionPage.goToPhylogenomicsPipeline(driver);

		assertTrue("Should display a warning to the user that there are no reference files.",
				page.isNoReferenceWarningDisplayed());
		assertTrue("User should be told that they can upload files", page.isAddReferenceFileLinksDisplayed());
		assertEquals("There should be a link to one project to upload a reference file", 1,
				page.getAddReferenceFileToProjectLinkCount());
	}

	@Test
	public void testPipelineSubmission() {
		addSamplesToCart();

		page.clickLaunchPipelineBtn();
		assertTrue("Message should be displayed when the pipeline is submitted", page.isPipelineSubmittedMessageShown());
		assertTrue("Message should be displayed once the pipeline finished submitting",
				page.isPipelineSubmittedSuccessMessageShown());
	}

	@Test
	public void testCheckPipelineStatusAfterSubmit() {
		addSamplesToCart();

		page.clickLaunchPipelineBtn();
		assertTrue("Message should be displayed once the pipeline finished submitting",
				page.isPipelineSubmittedSuccessMessageShown());
		page.clickSeePipeline();

		assertTrue("Should be on analysis page", driver.getCurrentUrl().endsWith("/analysis/list"));
	}

	@Test
	public void testClearPipelineAndGetSamples() {
		addSamplesToCart();

		page.clickLaunchPipelineBtn();
		assertTrue("Message should be displayed once the pipeline finished submitting",
				page.isPipelineSubmittedSuccessMessageShown());
		page.clickClearAndFindMore();

		assertTrue("Should be on projects page", driver.getCurrentUrl().endsWith("/projects"));
		assertEquals("cart should be empty", 0, page.getCartCount());
	}

	@Test
	public void testRemoveSample() {
		addSamplesToCart();

		int numberOfSamplesDisplayed = page.getNumberOfSamplesDisplayed();

		page.removeFirstSample();
		int laterNumber = page.getNumberOfSamplesDisplayed();

		assertEquals("should have 1 less sample than before", numberOfSamplesDisplayed - 1, laterNumber);
		assertEquals("cart samples count should equal samples on page", laterNumber, page.getCartCount());
	}

	@Test
	public void testRemoveAllSample() {
		addSamplesToCart();

		page.removeFirstSample();
		page.removeFirstSample();

		assertTrue("user should be redirected to pipelinese page", driver.getCurrentUrl().endsWith("/pipelines"));
	}

	@Test
	public void testModifyParameters() {
		addSamplesToCart();
		page.clickPipelineParametersBtn();
		assertEquals("Should have the proper pipeline name in title", "Default Parameters",
				page.getParametersModalTitle());

		// set the value for the ALternative Allele Fraction
		String value = page.getAlternativeAlleleFractionValue();
		String newValue = "10";
		page.setAlternativeAlleleFraction(newValue);
		assertEquals("Should not have the same value as the default after being changed", newValue,
				page.getAlternativeAlleleFractionValue());
		page.clickSetDefaultAlternativeAlleleFraction();
		assertEquals("Value should be reset to the default value", value, page.getAlternativeAlleleFractionValue());
	}

	@Test
	public void testModifyParametersAgain() throws InterruptedException {
		addSamplesToCart();
		page.clickPipelineParametersBtn();
		assertEquals("Should have the proper pipeline name in title", "Default Parameters",
				page.getParametersModalTitle());

		// set the value for the ALternative Allele Fraction
		String newValue = "10";
		page.setAlternativeAlleleFraction(newValue);
		assertEquals("Should be set to the new value.", newValue, page.getAlternativeAlleleFractionValue());

		page.clickUseParametersButton();

		// open the dialog again and make sure that the changed values are still
		// there:
		page.clickPipelineParametersBtn();
		assertEquals("alternative allele fraction should have the same value as the new value after being changed",
				newValue, page.getAlternativeAlleleFractionValue());
	}

	@Test
	public void testModifyAndSaveParameters() {
		addSamplesToCart();
		page.clickPipelineParametersBtn();
		assertEquals("Should have the proper pipeline name in title", "Default Parameters",
				page.getParametersModalTitle());

		// set the value for the ALternative Allele Fraction
		String newValue = "10";
		final String savedParametersName = "Saved parameters name.";
		page.setAlternativeAlleleFraction(newValue);
		assertEquals("Should have updated alternative allele fractiion value to new value.", newValue,
				page.getAlternativeAlleleFractionValue());
		page.clickSaveParameters();
		assertTrue("Page should have shown name for parameters field with selected parameters name.",
				page.isNameForParametersVisible());
		page.setNameForSavedParameters(savedParametersName);
		page.clickUseParametersButton();
		assertEquals("Selected parameter set should be the saved one.", savedParametersName,
				page.getSelectedParameterSet());
	}

	private void addSamplesToCart() {
		LoginPage.loginAsUser(driver);
		ProjectSamplesPage samplesPage = new ProjectSamplesPage(driver);
		samplesPage.goToPage("1");
		samplesPage.selectSampleByRow(0);
		samplesPage.selectSampleByRow(1);
		samplesPage.addSamplesToGlobalCart();
		PipelinesSelectionPage.goToPhylogenomicsPipeline(driver);
	}
}
