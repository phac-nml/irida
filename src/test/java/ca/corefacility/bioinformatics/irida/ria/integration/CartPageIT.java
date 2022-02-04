package ca.corefacility.bioinformatics.irida.ria.integration;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import ca.corefacility.bioinformatics.irida.ria.integration.components.SampleDetailsViewer;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.cart.CartPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSamplesPage;
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
			List.of("test_file.fastq", "test_file_1.fastq", "test_file_2.fastq"));

	@Autowired
	@Qualifier("sequenceFileBaseDirectory")
	private Path sequenceFileBaseDirectory;

	@BeforeEach
	// Move file to the sequenceFileBaseDirectory from the test folder so it can be accessed by the tests
	public void setFile() throws IOException {
		for(String fileName : fileNames) {
			fileUtilities.copyFileToDirectory(sequenceFileBaseDirectory, "src/test/resources/files/" + fileName);
		}
	}

	@Test
	public void testCartPageAsUser() {
		LoginPage.loginAsUser(driver());

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
		page.viewSampleDetailsFor(sampleName);
		SampleDetailsViewer sampleDetailsViewer = SampleDetailsViewer.getSampleDetails(driver());

		assertEquals(sampleName, sampleDetailsViewer.getSampleName(), "Should be viewing the proper sample");
//		assertEquals("Jul 19, 2013, 2:18 PM", sampleDetailsViewer.getCreatedDateForSample(), "Should display the correct created date");

		sampleDetailsViewer.clickMetadataTabLink();
		assertFalse(sampleDetailsViewer.addNewMetadataButtonVisible());
		assertEquals(4, sampleDetailsViewer.getNumberOfMetadataEntries(), "Should have the proper number of metadata entries");
		assertEquals("AB-1003", sampleDetailsViewer.getValueForMetadataField("symptom"), "Should be able to diplay the proper metadata");


		sampleDetailsViewer.clickFilesTabLink();
		// Check that the upload option is not available to a user on a project (only project owner should be able to view)
		assertFalse(sampleDetailsViewer.fileUploadVisible(), "Drag upload should not be visible to user");

		// Check that correct file list items are displayed for the user
		assertEquals(3, sampleDetailsViewer.numberOfFilesDisplayed(), "Three files should be displayed for sample");
		assertTrue(sampleDetailsViewer.correctFileNamesDisplayedUser(fileNames), "Correct file labels should be displayed for uploaded files");
		assertEquals(3, sampleDetailsViewer.processingStatusesCount(), "Three files should have processing statuses displayed");
		assertEquals(0, sampleDetailsViewer.removeFileButtonsVisible(), "Shouldn't have any file remove buttons");
		assertEquals(0, sampleDetailsViewer.concatenationCheckboxesVisible(), "Shouldn't have any concatenation checkboxes");
		assertEquals(3, sampleDetailsViewer.downloadFileButtonsVisible(), "Should have 3 download file buttons");

		// No files should be selected (no checkboxes available to select) and the concatenate button should not be visible
		sampleDetailsViewer.selectFilesToConcatenate();
		assertFalse(sampleDetailsViewer.concatenationButtonVisible());
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
		page.viewSampleDetailsFor(sampleName);
		SampleDetailsViewer sampleDetailsViewer = SampleDetailsViewer.getSampleDetails(driver());

		assertEquals(sampleName, sampleDetailsViewer.getSampleName(), "Should be viewing the proper sample");
		//		assertEquals("Jul 19, 2013, 2:18 PM", sampleDetailsViewer.getCreatedDateForSample(), "Shoauld display the correct created date");

		sampleDetailsViewer.clickMetadataTabLink();
		assertTrue(sampleDetailsViewer.addNewMetadataButtonVisible());
		assertEquals(4, sampleDetailsViewer.getNumberOfMetadataEntries(), "Should have the proper number of metadata entries");
		assertEquals("AB-1003", sampleDetailsViewer.getValueForMetadataField("symptom"), "Should be able to display the proper metadata");

		sampleDetailsViewer.clickFilesTabLink();

		// Check that the upload option is visible
		assertTrue(sampleDetailsViewer.fileUploadVisible(), "Drag upload should be visible to user");

		// Check that correct file list items are displayed
		assertEquals(3, sampleDetailsViewer.numberOfFilesDisplayed(), "Three files should be displayed for sample");
		assertEquals(3, sampleDetailsViewer.processingStatusesCount(), "Three files should have processing statuses displayed");
		assertEquals(3, sampleDetailsViewer.removeFileButtonsVisible(), "Should have 3 file remove buttons");
		assertEquals(3, sampleDetailsViewer.concatenationCheckboxesVisible(), "Should have 3 concatenation checkboxes");
		assertEquals(3, sampleDetailsViewer.downloadFileButtonsVisible(), "Should have 3 download file buttons");

		// Checkboxes to select files for concatenation should be visible and selectable
		sampleDetailsViewer.selectFilesToConcatenate();
		// Concatenate button should be visible
		assertTrue(sampleDetailsViewer.concatenationButtonVisible());
		sampleDetailsViewer.clickConcatenateBtn();

		// Concatenation modal should list the files to concatenate
		assertEquals(3, sampleDetailsViewer.singleEndFileCount(), "Should have 3 single end files listed for concatenation");
		// Enter concatenation file name
		sampleDetailsViewer.enterFileName();
		sampleDetailsViewer.clickConcatenateConfirmBtn();

		// Check that correct file list items are displayed after concatenation
		assertEquals(4, sampleDetailsViewer.numberOfFilesDisplayed(), "Four files should be displayed for sample");
		assertTrue(sampleDetailsViewer.correctFileNamesDisplayedAdmin(fileNames), "Correct file labels should be displayed including the concatenated file");
		assertEquals(4, sampleDetailsViewer.processingStatusesCount(), "Four files should have processing statuses displayed");
		assertEquals(4, sampleDetailsViewer.removeFileButtonsVisible(), "Should have 4 file remove buttons");
		assertEquals(4, sampleDetailsViewer.concatenationCheckboxesVisible(), "Should have 4 concatenation checkboxes");
		assertEquals(4, sampleDetailsViewer.downloadFileButtonsVisible(), "Should have 4 download file buttons");

		// Checkboxes to select files for concatenation should be visible and selectable
		sampleDetailsViewer.selectFilesToConcatenate();
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
		assertEquals(1, sampleDetailsViewer.numberOfFilesDisplayed(), "One file should be displayed for sample after concatenation and removal of original files");
		assertTrue(sampleDetailsViewer.correctFileNamesDisplayedAdmin(fileNames, "AnotherConcatenatedFile"), "Correct file label should be displayed for the concatenated file");
		assertEquals(1, sampleDetailsViewer.processingStatusesCount(), "One file should have processing statuses displayed");
		assertEquals(1, sampleDetailsViewer.removeFileButtonsVisible(), "Should have 1 file remove button");
		assertEquals(0, sampleDetailsViewer.concatenationCheckboxesVisible(), "Should not have any concatenation checkboxes");
		assertEquals(1, sampleDetailsViewer.downloadFileButtonsVisible(), "Should have 1 download file buttons");

		// Remove the one remaining file
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