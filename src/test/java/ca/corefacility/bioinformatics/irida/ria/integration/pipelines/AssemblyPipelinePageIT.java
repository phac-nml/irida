package ca.corefacility.bioinformatics.irida.ria.integration.pipelines;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.cart.CartPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.Assert.*;

/**
 * Testing for launching an assembly pipeline.
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/pipelines/AssemblyPipelinePageIT.xml")
public class AssemblyPipelinePageIT extends BasePipelineLaunchPageIT {

	@Test
	public void testPageSetup() {
		CartPage cartPage = new CartPage(driver());
		cartPage.selectAssemblyPipeline();
		assertEquals("Launch Page should display  the pipeline name", "Assembly and Annotation Pipeline", page.getPipelineName());
		assertTrue("Launch form should be displayed", page.isLaunchFormDisplayed());
		assertTrue("Launch details should be displayed", page.isLaunchDetailsDisplayed());
		assertTrue("Launch parameters should be displayed", page.isLaunchParametersDisplayed());
		assertTrue("Share with samples should not be displayed", page.isShareWithSamplesDisplayed());
		assertTrue("Share with projects should be displayed", page.isShareWithProjectsDisplayed());
		assertFalse("Should be able to select a reference file", page.isReferenceFilesDisplayed());
		assertTrue("Should be able to select sample files", page.isLaunchFilesDisplayed());
		assertFalse("This pipeline does not need a reference file, so there should be none requested", page.isReferenceFilesRequiredDisplayed());

	}

//
//	@Autowired
//	private AnalysisSubmissionRepository analysisSubmissionRepository;
//
//	@Before
//	public void setUpTest() {
//		page = new BasicPipelinePage(driver());
//		cartPage = new CartPage(driver());
//	}
//
//	@Test
//	public void testPageSetup() {
//		addSamplesToCartManager();
//
//		logger.info("Checking Assembly Page Setup.");
//		assertEquals("Should display the correct number of samples.", 1, page.getNumberOfSamplesDisplayed());
//	}
//
//	@Test
//	public void testShareResultsWithSamples() {
//		addSamplesToCartManager();
//
//		String analysisName = AssemblyPipelinePageIT.class.getName() + ".testShareResultsWithSamples";
//		page.setNameForAnalysisPipeline(analysisName);
//
//		assertTrue("Share Results with Samples checkbox should exist", page.existsShareResultsWithSamples());
//		page.clickShareResultsWithSamples();
//		page.clickLaunchPipelineBtn();
//		assertTrue("Message should be displayed once the pipeline finished submitting",
//				page.isPipelineSubmittedSuccessMessageShown());
//
//		AnalysisSubmission submission = findAnalysisSubmissionWithName(analysisName + "_sample1");
//
//		assertNotNull("Analysis Submission is null", submission);
//		assertTrue("updateSamples should be true", submission.getUpdateSamples());
//	}
//
//	@Test
//	public void testNoShareResultsWithSamples() {
//		addSamplesToCartManager();
//
//		String analysisName = AssemblyPipelinePageIT.class.getName() + ".testNoShareResultsWithSamples";
//		page.setNameForAnalysisPipeline(analysisName);
//
//		assertTrue("Share Results with Samples checkbox should exist", page.existsShareResultsWithSamples());
//		page.clickLaunchPipelineBtn();
//		assertTrue("Message should be displayed once the pipeline finished submitting",
//				page.isPipelineSubmittedSuccessMessageShown());
//
//		AnalysisSubmission submission = findAnalysisSubmissionWithName(analysisName + "_sample1");
//
//		assertNotNull("Analysis Submission is null", submission);
//		assertFalse("updateSamples should be false", submission.getUpdateSamples());
//	}
//
//	@Test
//	public void testUserNoShareResultsWithSamples() {
//		addSamplesToCartUser();
//
//		String analysisName = AssemblyPipelinePageIT.class.getName() + ".testUserNoShareResultsWithSamples";
//		page.setNameForAnalysisPipeline(analysisName);
//
//		assertFalse("Share Results with Samples checkbox should not exist", page.existsShareResultsWithSamples());
//		page.clickLaunchPipelineBtn();
//		assertTrue("Message should be displayed once the pipeline finished submitting",
//				page.isPipelineSubmittedSuccessMessageShown());
//
//		AnalysisSubmission submission = findAnalysisSubmissionWithName(analysisName + "_sample1");
//
//		assertNotNull("Analysis Submission is null", submission);
//		assertFalse("updateSamples should be false", submission.getUpdateSamples());
//	}
//
//	private void addSamplesToCartUser() {
//		LoginPage.loginAsUser(driver());
//		addSamplesToCart();
//	}
//
//	private void addSamplesToCartManager() {
//		LoginPage.loginAsManager(driver());
//		addSamplesToCart();
//	}
//
//	private void addSamplesToCart() {
//		ProjectSamplesPage samplesPage = ProjectSamplesPage.gotToPage(driver(), 1);
//		samplesPage.selectSample(0);
//		samplesPage.addSelectedSamplesToCart();
//		cartPage.selectAssemblyPipeline();
//	}
//
//	private AnalysisSubmission findAnalysisSubmissionWithName(String name) {
//		AnalysisSubmission submission = null;
//		for (AnalysisSubmission s : analysisSubmissionRepository.findAll()) {
//			if (name.equals(s.getName())) {
//				submission = s;
//			}
//		}
//
//		return submission;
//	}
//
//	@Test
//	public void testEmailPipelineResult() {
//		addSamplesToCartManager();
//
//		String analysisName = AssemblyPipelinePageIT.class.getName() + ".testEmailPipelineResult";
//		page.setNameForAnalysisPipeline(analysisName);
//
//		assertTrue("Email Pipeline Result status select should exist", page.existsEmailPipelineResultSelect());
//		page.selectErrorFromEmailDropdown();
//
//		page.clickLaunchPipelineBtn();
//		assertTrue("Message should be displayed once the pipeline finished submitting",
//				page.isPipelineSubmittedSuccessMessageShown());
//
//		AnalysisSubmission submission = findAnalysisSubmissionWithName(analysisName + "_sample1");
//
//		assertNotNull("Analysis Submission is null", submission);
//		assertFalse("emailPipelineResultCompleted should be true", submission.getEmailPipelineResultCompleted());
//		assertTrue("emailPipelineResultError should be true", submission.getEmailPipelineResultError());
//	}
}
