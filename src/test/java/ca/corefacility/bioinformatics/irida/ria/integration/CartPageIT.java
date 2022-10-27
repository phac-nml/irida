package ca.corefacility.bioinformatics.irida.ria.integration;

import ca.corefacility.bioinformatics.irida.ria.integration.components.FastQCModal;
import ca.corefacility.bioinformatics.irida.ria.integration.components.SampleDetailsViewer;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.cart.CartPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSamplesPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.FileUtilities;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/CartView.xml")
public class CartPageIT extends AbstractIridaUIITChromeDriver {
	private FileUtilities fileUtilities = new FileUtilities();
	private List<String> fileNames = new ArrayList<>(
			List.of("test_file.fastq", "test_file_1.fastq", "test_file_2.fastq", "01-1111_S1_L001_R1_001.fastq",
					"02-2222_S1_L001_R2_001.fastq", "04-4444_S1_L001_R1_001.fastq", "04-4444_S1_L001_R2_001.fastq",
					"test_file.fasta", "test_file_2.fasta"));

	private List<String> singleFileNames = new ArrayList<>(
			List.of("test_file.fastq", "test_file_1.fastq", "test_file_2.fastq"));

	private List<String> pairedFileNames = new ArrayList<>(
			List.of("01-1111_S1_L001_R1_001.fastq", "02-2222_S1_L001_R2_001.fastq", "04-4444_S1_L001_R1_001.fastq",
					"04-4444_S1_L001_R2_001.fastq"));

	private List<String> assemblyFileNames = new ArrayList<>(List.of("test_file.fasta", "test_file_2.fasta"));

	private static final Logger logger = LoggerFactory.getLogger(CartPageIT.class);

	/*
	 * FILE ATTRIBUTES
	 */
	private static final String FILE_NAME = "test_file.fastq";
	private static final String FILE_ID = "1";
	private static final String FILE_ENCODING = "Sanger / Illumina 1.9";
	private static final String FILE_CREATED = "Jul 18, 2013, 2:20 PM";
	private static final String FILE_TOTAL_SEQUENCE = "4";
	private static final String FILE_TOTAL_BASES = "937";
	private static final String FILE_MIN_LENGTH = "184";
	private static final String FILE_MAX_LENGTH = "251";
	private static final String FILE_GC_CONTENT = "30";

	@Autowired
	@Qualifier("sequenceFileBaseDirectory")
	private Path sequenceFileBaseDirectory;

	@Autowired
	@Qualifier("assemblyFileBaseDirectory")
	private Path assemblyFileBaseDirectory;

	@Autowired
	@Qualifier("outputFileBaseDirectory")
	private Path outputFileBaseDirectory;

	@BeforeEach
	// Move file to the sequenceFileBaseDirectory from the test folder so it can be accessed by the tests
	public void setFile() throws IOException {
		for (String sFileName : singleFileNames) {
			fileUtilities.copyFileToDirectory(sequenceFileBaseDirectory, "src/test/resources/files/" + sFileName);
		}

		for (String pFileName : pairedFileNames) {
			fileUtilities.copyFileToDirectory(sequenceFileBaseDirectory,
					"src/test/resources/files/sequence-files/" + pFileName);
		}

		for (String aFileName : assemblyFileNames) {
			fileUtilities.copyFileToDirectory(assemblyFileBaseDirectory, "src/test/resources/files/" + aFileName);
		}

		try {
			fileUtilities.copyFileToDirectory(outputFileBaseDirectory,
					"src/test/resources/files/perBaseQualityScoreChart.png");
			fileUtilities.copyFileToDirectory(outputFileBaseDirectory,
					"src/test/resources/files/perSequenceQualityScoreChart.png");
			fileUtilities.copyFileToDirectory(outputFileBaseDirectory,
					"src/test/resources/files/duplicationLevelChart.png");
		} catch (IOException e) {
			logger.error("Cannot copy file. File not found.", e);
		}
	}

	@Test
	public void testCartPageAsUser() {
		LoginPage.loginAsUser(driver());
		driver().manage().window().maximize();
		// Add some samples to the cart and test to see if they get displayed/
		ProjectSamplesPage samplesPage = ProjectSamplesPage.goToPage(driver(), 1);
		samplesPage.selectSampleByName("sample5fg44");
		samplesPage.selectSampleByName("sample5fdgr");
		samplesPage.selectSampleByName("sample554sg5");
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

		assertFalse(sampleDetailsViewer.isAddSampleToCartButtonVisible(),
				"The add cart to sample button should not be displayed");
		assertTrue(sampleDetailsViewer.isRemoveSampleFromCartButtonVisible(),
				"The remove sample from cart button should be displayed");

		assertEquals(sampleName, sampleDetailsViewer.getSampleName(), "Should be viewing the proper sample");
		assertEquals(projectName, sampleDetailsViewer.getProjectName(),
				"Should have proper project name displayed for sample");
		assertEquals("Jul 19, 2013, 2:18 PM", sampleDetailsViewer.getCreatedDateForSample(), "Should display the correct created date");

		sampleDetailsViewer.clickMetadataTabLink();
		assertFalse(sampleDetailsViewer.addNewMetadataButtonVisible());
		assertEquals(4, sampleDetailsViewer.getNumberOfMetadataEntries(),
				"Should have the proper number of metadata entries");
		assertEquals("AB-1003", sampleDetailsViewer.getValueForMetadataField("symptom"),
				"Should be able to diplay the proper metadata");

		sampleDetailsViewer.clickFilesTabLink();
		// Check that the upload option is not available to a user on a project (only project owner should be able to view)
		assertFalse(sampleDetailsViewer.fileUploadVisible(), "Drag upload should not be visible to user");

		// Check that correct file list items are displayed for the user
		assertEquals(9, sampleDetailsViewer.numberOfFilesDisplayed(), "Nine files should be displayed for sample");
		assertTrue(sampleDetailsViewer.correctFileNamesDisplayedUser(fileNames),
				"Correct file labels should be displayed for uploaded files");
		assertEquals(7, sampleDetailsViewer.processingStatusesCount(),
				"Seven files should have processing statuses displayed");
		assertEquals(0, sampleDetailsViewer.concatenationCheckboxesVisible(),
				"Shouldn't have any concatenation checkboxes");
		assertEquals(9, sampleDetailsViewer.actionButtonsVisible(), "Should have 9 file action buttons");

		// Launch fastqc modal
		sampleDetailsViewer.clickSampleName();

		FastQCModal fastQCModal = FastQCModal.getFileFastQCDetails(driver());

		assertEquals(3, fastQCModal.getChartCount(), "Should display three charts");

		fastQCModal.clickFastQCOverrepresentedSequencesLink();
		overrepresentedSequencesTabInfo(fastQCModal);

		fastQCModal.clickFastQCDetailsLink();
		detailsTabInfo(fastQCModal);

		fastQCModal.closeFastqcModal();

		// No files should be selected (no checkboxes available to select) and the concatenate button should not be visible
		sampleDetailsViewer.selectFilesToConcatenate(3);
		assertFalse(sampleDetailsViewer.concatenationButtonVisible());

		sampleDetailsViewer.clickSampleAnalysesTabLink();
		assertTrue(sampleDetailsViewer.searchSampleAnalysesInputVisible());
		assertTrue(sampleDetailsViewer.sampleAnalysesTableVisible());
		assertEquals(1, sampleDetailsViewer.numberOfSampleAnalysesVisible(),
				"User should only see listing of 1 analysis ran with this sample");
		assertEquals(0, sampleDetailsViewer.filterSampleAnalyses("bioh"),
				"Filtering analyses by 'bio' should yield 0 results");
		sampleDetailsViewer.clearSampleAnalysesFilter();
		assertEquals(1, sampleDetailsViewer.numberOfSampleAnalysesVisible(),
				"AUser should only see listing of 1 analysis ran with this sample");

		sampleDetailsViewer.clickRemoveSampleFromCartButton();

		assertFalse(sampleDetailsViewer.sampleDetailsViewerVisible(), "The sample details viewer should not be displayed as the sample was removed from the cart");

		// Test removing a sample from the project
		page.removeSampleFromCart(0);
		assertEquals(1, page.getNumberOfSamplesInCart(), "Should be 1 sample in the cart");

		// Test removing the entire project
		page.removeProjectFromCart();
	}

	@Test
	public void testCartPageAsAdmin() {
		LoginPage.loginAsAdmin(driver());
		driver().manage().window().maximize();
		// Add some samples to the cart and test to see if they get displayed/
		ProjectSamplesPage samplesPage = ProjectSamplesPage.goToPage(driver(), 1);
		samplesPage.selectSampleByName("sample5fg44");
		samplesPage.selectSampleByName("sample5fdgr");
		samplesPage.selectSampleByName("sample554sg5");
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

		assertFalse(sampleDetailsViewer.isAddSampleToCartButtonVisible(),
				"The add cart to sample button should not be displayed");
		assertTrue(sampleDetailsViewer.isRemoveSampleFromCartButtonVisible(),
				"The remove sample from cart button should be displayed");

		assertEquals(sampleName, sampleDetailsViewer.getSampleName(), "Should be viewing the proper sample");

		assertEquals(projectName, sampleDetailsViewer.getProjectName(),
				"Should have proper project name displayed for sample");
		assertEquals("Jul 19, 2013, 2:18 PM", sampleDetailsViewer.getCreatedDateForSample(), "Should display the correct created date");

		sampleDetailsViewer.clickMetadataTabLink();
		assertTrue(sampleDetailsViewer.addNewMetadataButtonVisible());
		assertEquals(4, sampleDetailsViewer.getNumberOfMetadataEntries(),
				"Should have the proper number of metadata entries");
		assertEquals("AB-1003", sampleDetailsViewer.getValueForMetadataField("symptom"),
				"Should be able to display the proper metadata");

		sampleDetailsViewer.clickSampleAnalysesTabLink();
		assertTrue(sampleDetailsViewer.searchSampleAnalysesInputVisible());
		assertTrue(sampleDetailsViewer.sampleAnalysesTableVisible());
		assertEquals(5, sampleDetailsViewer.numberOfSampleAnalysesVisible(),
				"Admin should have a listing of all 5 analyses ran with this sample");
		assertEquals(1, sampleDetailsViewer.filterSampleAnalyses("bio"),
				"Filtering analyses by 'bio' should yield 0 results");
		sampleDetailsViewer.clearSampleAnalysesFilter();
		assertEquals(5, sampleDetailsViewer.numberOfSampleAnalysesVisible(),
				"Admin should see listing of 5 analyses ran with this sample after clearing search input");

		sampleDetailsViewer.clickFilesTabLink();

		// Check that the upload option is visible
		assertTrue(sampleDetailsViewer.fileUploadVisible(), "Drag upload should be visible to user");

		// Check that correct file list items are displayed
		assertEquals(9, sampleDetailsViewer.numberOfFilesDisplayed(), "Nine files should be displayed for sample");
		assertEquals(7, sampleDetailsViewer.processingStatusesCount(),
				"Seven files should have processing statuses displayed");
		assertEquals(5, sampleDetailsViewer.concatenationCheckboxesVisible(), "Should have 5 concatenation checkboxes");
		assertEquals(9, sampleDetailsViewer.actionButtonsVisible(), "Should have 9 file action buttons");

		// Launch fastqc modal
		sampleDetailsViewer.clickSampleName();

		FastQCModal fastQCModal = FastQCModal.getFileFastQCDetails(driver());

		assertEquals(3, fastQCModal.getChartCount(), "Should display three charts");

		fastQCModal.clickFastQCOverrepresentedSequencesLink();
		overrepresentedSequencesTabInfo(fastQCModal);

		fastQCModal.clickFastQCDetailsLink();
		detailsTabInfo(fastQCModal);

		fastQCModal.closeFastqcModal();

		// Checkboxes to select files for concatenation should be visible and selectable
		sampleDetailsViewer.selectFilesToConcatenate(3);
		// Concatenate button should be visible
		assertTrue(sampleDetailsViewer.concatenationButtonVisible());
		sampleDetailsViewer.clickConcatenateBtn();

		// Concatenation modal should list the files to concatenate
		assertEquals(3, sampleDetailsViewer.singleEndFileCount(),
				"Should have 3 single end files listed for concatenation");
		// Enter concatenation file name
		sampleDetailsViewer.enterFileName();
		sampleDetailsViewer.clickConcatenateConfirmBtn();

		// Check that correct file list items are displayed after concatenation
		assertEquals(10, sampleDetailsViewer.numberOfFilesDisplayed(), "Ten files should be displayed for sample");
		assertTrue(sampleDetailsViewer.correctFileNamesDisplayedAdmin(fileNames),
				"Correct file labels should be displayed including the concatenated file");
		assertEquals(8, sampleDetailsViewer.processingStatusesCount(),
				"Eight files should have processing statuses displayed");

		assertEquals(6, sampleDetailsViewer.concatenationCheckboxesVisible(), "Should have 6 concatenation checkboxes");
		assertEquals(10, sampleDetailsViewer.actionButtonsVisible(), "Should have 10 download file buttons");

		assertTrue(sampleDetailsViewer.setSetDefaultSeqObjButtonsVisible());

		JavascriptExecutor js = (JavascriptExecutor) driver();
		js.executeScript("document.getElementsByClassName('t-filelist-scroll')[0].scrollTop= 200");

		assertEquals(1, sampleDetailsViewer.defaultSeqObjTagCount(),
				"Only one sequencing object should be designated as the default sequencing object for the sample");

		sampleDetailsViewer.updateDefaultSequencingObjectForSample();

		js.executeScript("document.getElementsByClassName('t-filelist-scroll')[0].scrollTop= 0");
		// Checkboxes to select files for concatenation should be visible and selectable
		sampleDetailsViewer.selectFilesToConcatenate(4);
		// Concatenate button should be visible
		assertTrue(sampleDetailsViewer.concatenationButtonVisible());
		sampleDetailsViewer.clickConcatenateBtn();

		// Concatenation modal should list the files to concatenate
		assertEquals(4, sampleDetailsViewer.singleEndFileCount(),
				"Should have 4 single end files listed for concatenation");
		// Enter concatenation file name
		sampleDetailsViewer.enterFileName("AnotherConcatenatedFile");
		// Select to remove the original files
		sampleDetailsViewer.clickRemoveOriginalsRadioButton();
		sampleDetailsViewer.clickConcatenateConfirmBtn();

		// Check that correct file list items are displayed after concatenation
		assertEquals(7, sampleDetailsViewer.numberOfFilesDisplayed(),
				"Seven files should be displayed for sample after concatenation and removal of original files");
		assertTrue(sampleDetailsViewer.correctFileNamesDisplayedAdmin(fileNames, "AnotherConcatenatedFile"),
				"Correct file label should be displayed for the concatenated file");
		assertEquals(5, sampleDetailsViewer.processingStatusesCount(),
				"Five files should have processing statuses displayed");
		assertEquals(2, sampleDetailsViewer.concatenationCheckboxesVisible(),
				"Should not have any concatenation checkboxes");
		assertEquals(7, sampleDetailsViewer.actionButtonsVisible(), "Should have 7 file action buttons");

		assertEquals(1, sampleDetailsViewer.numberOfSequencingObjectsSetAsDefault(), "One sequencing object should have a default tag");
		assertEquals(2, sampleDetailsViewer.numberOfSetAsDefaultSeqObjsButtons(), "There should be two set as default buttons for sequencing objects");

		// Remove the 5 remaining files (1 single end sequencing object and 2 paired end sequencing objects containing 2 files each, and 2 assemblies)
		js.executeScript("document.getElementsByClassName('t-filelist-scroll')[0].scrollTop= 600");

		assertEquals(1, sampleDetailsViewer.numberOfGenomeAssembliesSetAsDefault(), "One sequencing object should have a default tag");
		assertEquals(1, sampleDetailsViewer.numberOfGenomeAssembliesSetAsDefaultButtons(), "There should be one set as default button for sequencing objects");

		sampleDetailsViewer.removeFile(6);
		sampleDetailsViewer.removeFile(5);
		sampleDetailsViewer.removeFile(3);
		sampleDetailsViewer.removeFile(1);
		sampleDetailsViewer.removeFile(0);

		assertEquals(0, sampleDetailsViewer.numberOfFilesDisplayed(),
				"No files should be left for sample after file removal");

		assertTrue(sampleDetailsViewer.isRemoveSampleFromCartButtonVisible(),
				"The remove sample from cart button should be displayed");

		sampleDetailsViewer.clickRemoveSampleFromCartButton();

		assertFalse(sampleDetailsViewer.sampleDetailsViewerVisible(), "The sample details viewer should not be displayed as the sample was removed from the cart");

		// Test removing a sample from the project
		page.removeSampleFromCart(0);
		assertEquals(1, page.getNumberOfSamplesInCart(), "Should be 1 samples in the cart");

		// Test removing the entire project
		page.removeProjectFromCart();
	}

	private void overrepresentedSequencesTabInfo(FastQCModal fastQCModal) {
		assertEquals(1, fastQCModal.getNumberOfOverrepresentedSequences(), "Should display 1 overrepresented sequence");
		assertTrue(fastQCModal.getOverrepresentedSequence().matches("^[aAtTgGcC]+$"), "Should display a sequence");
		assertTrue(fastQCModal.getOverrepresentedSequencePercentage().contains("%"),
				"Should display the percentage with a percent sign");
		assertEquals("1", fastQCModal.getOverrepresentedSequenceCount(), "Should display the count");
		assertEquals("No Hit", fastQCModal.getOverrepresentedSequenceSource(), "Should display the source");
	}

	private void detailsTabInfo(FastQCModal fastQCModal) {
		logger.debug("Testing the Sequence File Overrepresented Sequence Page");
		assertEquals(FILE_NAME, fastQCModal.getFastQCFileTitle(), "Has the file name as the title");
		assertEquals(FILE_ID, fastQCModal.getFileId(), "Display the file id");
		assertEquals(FILE_CREATED, fastQCModal.getFileCreatedDate(), "Displays the file created date");
		assertEquals(FILE_ENCODING, fastQCModal.getFileEncoding(), "Displays the file encoding");
		assertEquals(FILE_TOTAL_SEQUENCE, fastQCModal.getTotalSequenceCount(), "Display the total sequence count");
		assertEquals(FILE_TOTAL_BASES, fastQCModal.getTotalBasesCount(), "Display the total bases count");
		assertEquals(FILE_MIN_LENGTH, fastQCModal.getMinLength(), "Displays the minLength");
		assertEquals(FILE_MAX_LENGTH, fastQCModal.getMaxLength(), "Displays the maxLength");
		assertEquals(FILE_GC_CONTENT, fastQCModal.getGCContent(), "Displays the gc content");
	}

}