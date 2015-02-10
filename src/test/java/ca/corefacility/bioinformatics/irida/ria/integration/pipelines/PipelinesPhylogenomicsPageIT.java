package ca.corefacility.bioinformatics.irida.ria.integration.pipelines;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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
 * <p> Testing for launching a phylogenomics pipeline. </p>
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
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
	private WebDriver driver;
	private PipelinesPhylogenomicsPage page;

	@Before
	public void setUp() {
		driver = TestUtilities.setDriverDefaults(new ChromeDriver());
		page = new PipelinesPhylogenomicsPage(driver);
	}

	@After
	public void destroy() {
		driver.quit();
	}

	@Test
	public void testPageSetup() {
		LoginPage.loginAsUser(driver);
		ProjectSamplesPage samplesPage = new ProjectSamplesPage(driver);
		samplesPage.goToPage();
		samplesPage.selectSampleByRow(1);
		samplesPage.selectSampleByRow(2);
		samplesPage.addSamplesToGlobalCart();
		PipelinesSelectionPage.goToPhylogenomicsPipeline(driver);
		logger.info("Checking Phylogenomics Page Setup.");
		assertTrue("Should be on the phylogenomics page.", driver.getCurrentUrl().contains(PipelinesPhylogenomicsPage.RELATIVE_URL));
		assertEquals("Should display the correct number of reference files in the select input.", 2, page.getReferenceFileCount());
		assertEquals("Should display the correct number of samples.", 2, page.getNumberofSamplesDisplayed());
	}

	@Test
	public void testNoRefFileNoPermissions() {
		LoginPage.loginAsUser(driver);

		// Add sample from a project that user is a "Project User" and has no reference files.
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
	}

	@Test
	public void testNoRefFileWithPermissions() {
		LoginPage.loginAsAdmin(driver);
		addSamplesToCart();
		PipelinesSelectionPage.goToPhylogenomicsPipeline(driver);

		assertTrue("Should display a warning to the user that there are no reference files.",
				page.isNoReferenceWarningDisplayed());
		assertTrue("User should be told that they can upload files", page.isAddReferenceFileLinksDisplayed());
		assertEquals("There should be a link to one project to upload a reference file", 1, page.getAddReferenceFileToProjectLinkCount());
	}

	@Test
	public void testPipelineSubmission() {
		LoginPage.loginAsUser(driver);
		PipelinesSelectionPage.goToPhylogenomicsPipeline(driver);
		assertTrue("Should be on the phylogenomics page.",
				driver.getCurrentUrl().contains(PipelinesPhylogenomicsPage.RELATIVE_URL));

		page.clickLaunchPipelineBtn();
		assertTrue("Message should be displayed when the pipeline is submitted", page.isPipelineSubmittedMessageShown());
		assertTrue("Message should be displayed once the pipeline finished submitting", page.isPipelineSubmittedSuccessMessageShown());
	}

	@Test
	public void testModifyParameters() {
		LoginPage.loginAsUser(driver);
		addSamplesToCart();
		PipelinesSelectionPage.goToPhylogenomicsPipeline(driver);
		page.clickPipelineParametersBtn();
		assertEquals("Should have the proper pipeline name in title", "Phylogenomics Pipeline Parameters",
				page.getParametersModalTitle());
	}

	private void addSamplesToCart() {
		ProjectSamplesPage samplesPage = new ProjectSamplesPage(driver);
		samplesPage.goToPage();
		samplesPage.selectSampleByRow(1);
		samplesPage.selectSampleByRow(2);
		samplesPage.addSamplesToGlobalCart();
	}
}
