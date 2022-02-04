package ca.corefacility.bioinformatics.irida.ria.integration;

import java.io.IOException;
import java.nio.file.Path;

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
	private final FileUtilities fileUtilities = new FileUtilities();

	@Autowired
	@Qualifier("sequenceFileBaseDirectory")
	private Path sequenceFileBaseDirectory;

	@BeforeEach
	// Tree file used by multiple tests
	public void setFile() throws IOException {
		fileUtilities.copyFileToDirectory(sequenceFileBaseDirectory, "src/test/resources/files/test_file.fastq");
		fileUtilities.copyFileToDirectory(sequenceFileBaseDirectory, "src/test/resources/files/test_file_1.fastq");
		fileUtilities.copyFileToDirectory(sequenceFileBaseDirectory, "src/test/resources/files/test_file_2.fastq");
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
		assertFalse(sampleDetailsViewer.fileUploadVisible(), "Drag upload should not be visible to user");
		assertEquals(3, sampleDetailsViewer.numberOfFilesDisplayed(), "Three files should be displayed for sample");
		assertEquals(3, sampleDetailsViewer.processingStatusesCount(), "Three files should have processing statuses displayed");
		assertEquals(0, sampleDetailsViewer.removeFileButtonsVisible(), "Shouldn't have any file remove buttons");
		assertEquals(0, sampleDetailsViewer.concatenationCheckboxesVisible(), "Shouldn't have any concatenation checkboxes");
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
		assertTrue(sampleDetailsViewer.fileUploadVisible(), "Drag upload should be visible to user");
		assertEquals(3, sampleDetailsViewer.numberOfFilesDisplayed(), "Three files should be displayed for sample");
		assertEquals(3, sampleDetailsViewer.processingStatusesCount(), "Three files should have processing statuses displayed");
		assertEquals(3, sampleDetailsViewer.removeFileButtonsVisible(), "Should have 3 file remove buttons");
		assertEquals(3, sampleDetailsViewer.concatenationCheckboxesVisible(), "Should have 3 concatenation checkboxes");
		sampleDetailsViewer.selectFilesToConcatenate();
		assertTrue(sampleDetailsViewer.concatenationButtonVisible());
		sampleDetailsViewer.clickConcatenateBtn();

		assertEquals(3, sampleDetailsViewer.singleEndFileCount(), "Should have 3 single end files listed for concatenation");
		sampleDetailsViewer.enterFileName();
		sampleDetailsViewer.clickConcatenateConfirmBtn();

		assertEquals(4, sampleDetailsViewer.numberOfFilesDisplayed(), "Four files should be displayed for sample");
		assertEquals(4, sampleDetailsViewer.processingStatusesCount(), "Four files should have processing statuses displayed");
		assertEquals(4, sampleDetailsViewer.removeFileButtonsVisible(), "Should have 4 file remove buttons");
		assertEquals(4, sampleDetailsViewer.concatenationCheckboxesVisible(), "Should have 4 concatenation checkboxes");

		sampleDetailsViewer.closeDetails();

		// Test removing a sample from the project
		page.removeSampleFromCart(0);
		assertEquals(2, page.getNumberOfSamplesInCart(), "Should be 2 samples in the cart");

		// Test removing the entire project
		page.removeProjectFromCart();
	}
}