package ca.corefacility.bioinformatics.irida.ria.integration;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import ca.corefacility.bioinformatics.irida.ria.integration.components.SampleDetailsViewer;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.cart.CartPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSamplesPage;
import ca.corefacility.bioinformatics.irida.ria.integration.sequenceFiles.SequenceFilePageIT;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.FileUtilities;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.ImmutableList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/CartView.xml")
public class CartPageIT extends AbstractIridaUIITChromeDriver {
	private FileUtilities fileUtilities = new FileUtilities();
	private List<String> fileNames = new ArrayList<>(
			List.of("test_file.fastq", "test_file_1.fastq", "test_file_2.fastq", "01-1111_S1_L001_R1_001.fastq", "02-2222_S1_L001_R2_001.fastq", "04-4444_S1_L001_R1_001.fastq", "04-4444_S1_L001_R2_001.fastq"));

	private List<String> singleFileNames = new ArrayList<>(
			List.of("test_file.fastq", "test_file_1.fastq", "test_file_2.fastq"));

	private List<String> pairedFileNames = new ArrayList<>(
			List.of("01-1111_S1_L001_R1_001.fastq", "02-2222_S1_L001_R2_001.fastq", "04-4444_S1_L001_R1_001.fastq", "04-4444_S1_L001_R2_001.fastq"));

	private static final Logger logger = LoggerFactory.getLogger(CartPageIT.class);

	@Autowired
	@Qualifier("sequenceFileBaseDirectory")
	private Path sequenceFileBaseDirectory;

	@Autowired
	@Qualifier("outputFileBaseDirectory")
	private Path outputFileBaseDirectory;

	@BeforeEach
	// Move file to the sequenceFileBaseDirectory from the test folder so it can be accessed by the tests
	public void setFile() throws IOException {
		for(String sFileName : singleFileNames) {
			fileUtilities.copyFileToDirectory(sequenceFileBaseDirectory, "src/test/resources/files/" + sFileName);
		}

		for(String pFileName : pairedFileNames) {
			fileUtilities.copyFileToDirectory(sequenceFileBaseDirectory, "src/test/resources/files/sequence-files/" + pFileName);
		}

		try {
			fileUtilities.copyFileToDirectory(outputFileBaseDirectory,
					"src/test/resources/files/perBaseQualityScoreChart.png");
			fileUtilities.copyFileToDirectory(outputFileBaseDirectory,
					"src/test/resources/files/perSequenceQualityScoreChart.png");
			fileUtilities.copyFileToDirectory(outputFileBaseDirectory,
					"src/test/resources/files/duplicationLevelChart.png");
		} catch(IOException e) {
			logger.error("Cannot copy file. File not found.", e);
		}
	}

	@Test
	public void testCartPageAsUser() {
		LoginPage.loginAsUser(driver());
		driver().manage().window().maximize();
		// Add some samples to the cart and test to see if they get displayed/
		ProjectSamplesPage samplesPage = ProjectSamplesPage.gotToPage(driver(), 1);
		samplesPage.selectSample(0);
		samplesPage.selectSample(2);
		samplesPage.selectSample(3);
		samplesPage.addSelectedSamplesToCart();

		// Make sure 3 samples are in the cart
		CartPage page = CartPage.goToCart(driver());
		checkTranslations(page, ImmutableList.of("cart"), null);
		int count = page.getNavBarSamplesCount();
		assertEquals(3, count, "Should be 3 samples displayed in navbar");
		assertEquals(3, page.getNumberOfSamplesInCart(), "Should be 3 samples in the cart");
		assertTrue(page.onPipelinesView(), "Should be directed to pipelines view");

		/*
		Test the sample details within the cart
		 */
		final String sampleName = "sample554sg5";
		final String projectName = "project";
		page.viewSampleDetailsFor(sampleName);
		SampleDetailsViewer sampleDetailsViewer = SampleDetailsViewer.getSampleDetails(driver());

		assertEquals(sampleName, sampleDetailsViewer.getSampleName(), "Should be viewing the proper sample");
		assertEquals(projectName, sampleDetailsViewer.getProjectName(), "Should have proper project name displayed for sample");
//		assertEquals("Jul 19, 2013, 2:18 PM", sampleDetailsViewer.getCreatedDateForSample(), "Should display the correct created date");

		sampleDetailsViewer.clickMetadataTabLink();
		assertFalse(sampleDetailsViewer.addNewMetadataButtonVisible());
		assertEquals(4, sampleDetailsViewer.getNumberOfMetadataEntries(), "Should have the proper number of metadata entries");
		assertEquals("AB-1003", sampleDetailsViewer.getValueForMetadataField("symptom"), "Should be able to diplay the proper metadata");


		sampleDetailsViewer.clickFilesTabLink();
		// Check that the upload option is not available to a user on a project (only project owner should be able to view)
		assertFalse(sampleDetailsViewer.fileUploadVisible(), "Drag upload should not be visible to user");

		// Check that correct file list items are displayed for the user
		assertEquals(7, sampleDetailsViewer.numberOfFilesDisplayed(), "Seven files should be displayed for sample");
		assertTrue(sampleDetailsViewer.correctFileNamesDisplayedUser(fileNames), "Correct file labels should be displayed for uploaded files");
		assertEquals(7, sampleDetailsViewer.processingStatusesCount(), "Seven files should have processing statuses displayed");
		assertEquals(0, sampleDetailsViewer.removeFileButtonsVisible(), "Shouldn't have any file remove buttons");
		assertEquals(0, sampleDetailsViewer.concatenationCheckboxesVisible(), "Shouldn't have any concatenation checkboxes");
		assertEquals(7, sampleDetailsViewer.downloadFileButtonsVisible(), "Should have 7 download file buttons");

		// No files should be selected (no checkboxes available to select) and the concatenate button should not be visible
		sampleDetailsViewer.selectFilesToConcatenate(3);
		assertFalse(sampleDetailsViewer.concatenationButtonVisible());

		sampleDetailsViewer.clickSampleAnalysesTabLink();
		assertTrue(sampleDetailsViewer.searchSampleAnalysesInputVisible());
		assertTrue(sampleDetailsViewer.sampleAnalysesTableVisible());
		assertEquals(1, sampleDetailsViewer.numberOfSampleAnalysesVisible(), "User should only see listing of 1 analysis ran with this sample");
		assertEquals(0, sampleDetailsViewer.filterSampleAnalyses("bio"), "Filtering analyses by 'bio' should yield 0 results");
		sampleDetailsViewer.clearSampleAnalysesFilter();
		assertEquals(1, sampleDetailsViewer.numberOfSampleAnalysesVisible(), "AUser should only see listing of 1 analysis ran with this sample");


		sampleDetailsViewer.closeDetails();

		// Test removing a sample from the project
		page.removeSampleFromCart(0);
		assertEquals(2, page.getNumberOfSamplesInCart(), "Should be 2 samples in the cart");

		// Test removing the entire project
		page.removeProjectFromCart();
	}

	@Test
	public void testCartPageAsAdmin() {
		LoginPage.loginAsAdmin(driver());
		driver().manage().window().maximize();
		// Add some samples to the cart and test to see if they get displayed/
		ProjectSamplesPage samplesPage = ProjectSamplesPage.gotToPage(driver(), 1);
		samplesPage.selectSample(0);
		samplesPage.selectSample(2);
		samplesPage.selectSample(3);
		samplesPage.addSelectedSamplesToCart();

		// Make sure 3 samples are in the cart
		CartPage page = CartPage.goToCart(driver());
		checkTranslations(page, ImmutableList.of("cart"), null);
		int count = page.getNavBarSamplesCount();
		assertEquals(3, count, "Should be 3 samples displayed in navbar");
		assertEquals(3, page.getNumberOfSamplesInCart(), "Should be 3 samples in the cart");
		assertTrue(page.onPipelinesView(), "Should be directed to pipelines view");

		/*
		Test the sample details within the cart
		 */
		final String sampleName = "sample554sg5";
		final String projectName = "project";
		page.viewSampleDetailsFor(sampleName);
		SampleDetailsViewer sampleDetailsViewer = SampleDetailsViewer.getSampleDetails(driver());

		assertEquals(sampleName, sampleDetailsViewer.getSampleName(), "Should be viewing the proper sample");
		assertEquals(projectName, sampleDetailsViewer.getProjectName(), "Should have proper project name displayed for sample");
//		assertEquals("Jul 19, 2013, 2:18 PM", sampleDetailsViewer.getCreatedDateForSample(), "Shoauld display the correct created date");

		sampleDetailsViewer.clickMetadataTabLink();
		assertTrue(sampleDetailsViewer.addNewMetadataButtonVisible());
		assertEquals(4, sampleDetailsViewer.getNumberOfMetadataEntries(), "Should have the proper number of metadata entries");
		assertEquals("AB-1003", sampleDetailsViewer.getValueForMetadataField("symptom"), "Should be able to display the proper metadata");

		sampleDetailsViewer.clickSampleAnalysesTabLink();
		assertTrue(sampleDetailsViewer.searchSampleAnalysesInputVisible());
		assertTrue(sampleDetailsViewer.sampleAnalysesTableVisible());
		assertEquals(5, sampleDetailsViewer.numberOfSampleAnalysesVisible(), "Admin should have a listing of all 5 analyses ran with this sample");
		assertEquals(1, sampleDetailsViewer.filterSampleAnalyses("bio"), "Filtering analyses by 'bio' should yield 0 results");
		sampleDetailsViewer.clearSampleAnalysesFilter();
		assertEquals(5, sampleDetailsViewer.numberOfSampleAnalysesVisible(), "Admin should see listing of 5 analyses ran with this sample after clearing search input");

		sampleDetailsViewer.clickFilesTabLink();

		// Check that the upload option is visible
		assertTrue(sampleDetailsViewer.fileUploadVisible(), "Drag upload should be visible to user");

		// Check that correct file list items are displayed
		assertEquals(7, sampleDetailsViewer.numberOfFilesDisplayed(), "Seven files should be displayed for sample");
		assertEquals(7, sampleDetailsViewer.processingStatusesCount(), "Seven files should have processing statuses displayed");
		assertEquals(5, sampleDetailsViewer.removeFileButtonsVisible(), "Should have 5 file remove buttons");
		assertEquals(5, sampleDetailsViewer.concatenationCheckboxesVisible(), "Should have 5 concatenation checkboxes");
		assertEquals(7, sampleDetailsViewer.downloadFileButtonsVisible(), "Should have 5 download file buttons");

		// Launch fastqc modal
		sampleDetailsViewer.clickSampleName();
		assertEquals(3, sampleDetailsViewer.getChartCount(), "Should display three charts");
		sampleDetailsViewer.closeFastqcModal();

		// Checkboxes to select files for concatenation should be visible and selectable
		sampleDetailsViewer.selectFilesToConcatenate(3);
		// Concatenate button should be visible
		assertTrue(sampleDetailsViewer.concatenationButtonVisible());
		sampleDetailsViewer.clickConcatenateBtn();

		// Concatenation modal should list the files to concatenate
		assertEquals(3, sampleDetailsViewer.singleEndFileCount(), "Should have 3 single end files listed for concatenation");
		// Enter concatenation file name
		sampleDetailsViewer.enterFileName();
		sampleDetailsViewer.clickConcatenateConfirmBtn();

		// Check that correct file list items are displayed after concatenation
		assertEquals(8, sampleDetailsViewer.numberOfFilesDisplayed(), "Eight files should be displayed for sample");
		assertTrue(sampleDetailsViewer.correctFileNamesDisplayedAdmin(fileNames), "Correct file labels should be displayed including the concatenated file");
		assertEquals(8, sampleDetailsViewer.processingStatusesCount(), "Eight files should have processing statuses displayed");
		assertEquals(6, sampleDetailsViewer.removeFileButtonsVisible(), "Should have 6 file remove buttons");
		assertEquals(6, sampleDetailsViewer.concatenationCheckboxesVisible(), "Should have 6 concatenation checkboxes");
		assertEquals(8, sampleDetailsViewer.downloadFileButtonsVisible(), "Should have 8 download file buttons");

		assertTrue(sampleDetailsViewer.setSetDefaultSeqObjButtonsVisible());
		assertEquals(1, sampleDetailsViewer.defaultSeqObjTagCount(),
				"Only one sequencing object (file pair) should be designated as the default sequencing object for the sample");

		JavascriptExecutor js = (JavascriptExecutor) driver();
		js.executeScript("document.getElementsByClassName('t-filelist-scroll')[0].scrollTop= 450");

		sampleDetailsViewer.updateDefaultSequencingObjectForSample();

		js.executeScript("document.getElementsByClassName('t-filelist-scroll')[0].scrollTop= 0");
		// Checkboxes to select files for concatenation should be visible and selectable
		sampleDetailsViewer.selectFilesToConcatenate(4);
		// Concatenate button should be visible
		assertTrue(sampleDetailsViewer.concatenationButtonVisible());
		sampleDetailsViewer.clickConcatenateBtn();

		// Concatenation modal should list the files to concatenate
		assertEquals(4, sampleDetailsViewer.singleEndFileCount(), "Should have 4 single end files listed for concatenation");
		// Enter concatenation file name
		sampleDetailsViewer.enterFileName("AnotherConcatenatedFile");
		// Select to remove the original files
		sampleDetailsViewer.clickRemoveOriginalsRadioButton();
		sampleDetailsViewer.clickConcatenateConfirmBtn();

		// Check that correct file list items are displayed after concatenation
		assertEquals(5, sampleDetailsViewer.numberOfFilesDisplayed(), "Five files should be displayed for sample after concatenation and removal of original files");
		assertTrue(sampleDetailsViewer.correctFileNamesDisplayedAdmin(fileNames, "AnotherConcatenatedFile"), "Correct file label should be displayed for the concatenated file");
		assertEquals(5, sampleDetailsViewer.processingStatusesCount(), "Five files should have processing statuses displayed");
		assertEquals(3, sampleDetailsViewer.removeFileButtonsVisible(), "Should have 3 file remove buttons");
		assertEquals(2, sampleDetailsViewer.concatenationCheckboxesVisible(), "Should not have any concatenation checkboxes");
		assertEquals(5, sampleDetailsViewer.downloadFileButtonsVisible(), "Should have 5 download file buttons");

		// Remove the 3 remaining files (1 single end sequencing object and 2 paired end sequencing objects containing 2 files each)
		sampleDetailsViewer.removeFile(2);
		sampleDetailsViewer.removeFile(1);
		sampleDetailsViewer.removeFile(0);
		assertEquals(0, sampleDetailsViewer.numberOfFilesDisplayed(), "No files should be left for sample after file removal");

		sampleDetailsViewer.closeDetails();

		// Test removing a sample from the project
		page.removeSampleFromCart(0);
		assertEquals(2, page.getNumberOfSamplesInCart(), "Should be 2 samples in the cart");

		// Test removing the entire project
		page.removeProjectFromCart();
	}

}