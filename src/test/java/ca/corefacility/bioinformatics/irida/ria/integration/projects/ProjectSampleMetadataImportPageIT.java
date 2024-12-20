package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.components.SampleDetailsViewer;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.ProjectMembersPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectDeletePage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSampleMetadataImportPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSamplesPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.ImmutableList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/ProjectSampleMetadataView.xml")
public class ProjectSampleMetadataImportPageIT extends AbstractIridaUIITChromeDriver {
	private static final String GOOD_FILE_PATH = "src/test/resources/files/metadata-upload/good.xlsx";
	private static final String MIXED_FILE_PATH = "src/test/resources/files/metadata-upload/mixed.xlsx";
	private static final String INVALID_FILE_PATH = "src/test/resources/files/metadata-upload/invalid.xlsx";
	private static final String EMPTY_HEADERS_FILE_PATH = "src/test/resources/files/metadata-upload/empty_headers.xlsx";
	private static final String EMPTY_AND_DUPLICATE_HEADERS_FILE_PATH = "src/test/resources/files/metadata-upload/empty_and_duplicate_headers.xlsx";
	private static final String DUPLICATE_HEADERS_FILE_PATH = "src/test/resources/files/metadata-upload/duplicate_headers.xlsx";
	private static final String SAMPLE_NAME_COLUMN = "NLEP #";

	private static final String SAMPLE_NAME = "sample1";
	private static final Long PROJECT_ID = 1L;

	@BeforeEach
	public void init() {
		LoginPage.loginAsManager(driver());
		LoginPage.loginAsAdmin(driver2());
	}

	@Test
	public void testGoodFileAndHeaders() {
		ProjectSampleMetadataImportPage page = ProjectSampleMetadataImportPage.goToPage(driver());
		page.uploadMetadataFile(GOOD_FILE_PATH);
		page.selectSampleNameColumn(SAMPLE_NAME_COLUMN);
		page.goToReviewPage();
		assertEquals(4, page.getUpdateCount(), "Has incorrect amount of update sample rows");
		assertEquals(1, page.getNewCount(), "Has incorrect amount of new sample rows");

		/*
		Check formatting.  A special check for number column formatting has been added in July 2020.
		Expected: 2.2222 -> 2.22 (if formatting set to 2 decimals). Actual numbers from file, before formatting:
		   2.222222
           2.3666
           1.5689
           63.89756
           59.6666
		 */
		List<Double> values = ImmutableList.of(2.222222, 2.3666, 1.5689, 63.89756, 59.6666)
				.stream()
				.map(num -> BigDecimal.valueOf(num).setScale(2, RoundingMode.HALF_UP).doubleValue())
				.collect(Collectors.toList());
		List<String> formattedNumbers = page.getValuesForColumnByName("Numbers");
		formattedNumbers.forEach(num -> assertTrue(values.contains(Double.valueOf(num)),
				"Found " + num + " that was not formatted properly"));
	}

	@Test
	public void testMixedFileAndHeaders() {
		ProjectSampleMetadataImportPage page = ProjectSampleMetadataImportPage.goToPage(driver());
		page.uploadMetadataFile(MIXED_FILE_PATH);
		page.selectSampleNameColumn(SAMPLE_NAME_COLUMN);
		page.goToReviewPage();
		assertEquals(4, page.getUpdateCount(), "Has incorrect amount of update sample rows");
		assertEquals(3, page.getNewCount(), "Has incorrect amount of new sample rows");
	}

	@Test
	public void testSuccessfulUpload() {
		ProjectSampleMetadataImportPage page = ProjectSampleMetadataImportPage.goToPage(driver());
		page.uploadMetadataFile(GOOD_FILE_PATH);
		//		assertEquals("NLEP #", page.getValueForSelectedSampleNameColumn(),
		//				"Has incorrect pre-populated sample name header");
		//		page.goToReviewPage();
		page.selectSampleNameColumn(SAMPLE_NAME_COLUMN);
		page.goToReviewPage();
		page.goToCompletePage();
		assertTrue(page.isSuccessDisplayed(), "Success message did not display");
	}

	@Test
	public void testFailedUpload() {
		ProjectSampleMetadataImportPage page = ProjectSampleMetadataImportPage.goToPage(driver());
		page.uploadMetadataFile(INVALID_FILE_PATH);
		page.selectSampleNameColumn(SAMPLE_NAME_COLUMN);
		page.goToReviewPage();
		assertTrue(page.isAlertDisplayed(), "Validation message did not display");
	}

	@Test
	public void testEmptyHeaders() {
		ProjectSampleMetadataImportPage page = ProjectSampleMetadataImportPage.goToPage(driver());
		page.uploadMetadataFile(EMPTY_HEADERS_FILE_PATH);
		assertTrue(page.isAlertDisplayed(), "Validation message did not display");
	}

	@Test
	public void testDuplicateHeaders() {
		ProjectSampleMetadataImportPage page = ProjectSampleMetadataImportPage.goToPage(driver());
		page.uploadMetadataFile(DUPLICATE_HEADERS_FILE_PATH);
		assertTrue(page.isAlertDisplayed(), "Validation message did not display");
	}

	@Test
	public void testEmptyAndDuplicateHeaders() {
		ProjectSampleMetadataImportPage page = ProjectSampleMetadataImportPage.goToPage(driver());
		page.uploadMetadataFile(EMPTY_AND_DUPLICATE_HEADERS_FILE_PATH);
		assertTrue(page.isAlertDisplayed(), "Validation message did not display");
	}

	@Test
	public void testFailedUploadByDeletingProject() {
		//manager starts a metadata import
		ProjectSampleMetadataImportPage page = ProjectSampleMetadataImportPage.goToPage(driver());
		page.uploadMetadataFile(GOOD_FILE_PATH);
		page.selectSampleNameColumn(SAMPLE_NAME_COLUMN);
		page.goToReviewPage();

		//admin deletes project
		ProjectDeletePage deleteProjectPage = ProjectDeletePage.goTo(driver2(), PROJECT_ID);
		deleteProjectPage.clickConfirm();
		deleteProjectPage.deleteProject();

		//manager tries to complete metadata import
		page.clickUploadButton();
		assertTrue(page.isErrorNotificationDisplayed(), "Error notification did not display");
	}

	@Test
	public void testFailedUploadByDeletingSamples() {
		//manager starts a metadata import
		ProjectSampleMetadataImportPage page = ProjectSampleMetadataImportPage.goToPage(driver());
		page.uploadMetadataFile(GOOD_FILE_PATH);
		page.selectSampleNameColumn(SAMPLE_NAME_COLUMN);
		page.goToReviewPage();

		//admin deletes samples
		ProjectSamplesPage projectSamplesPage = ProjectSamplesPage.goToPage(driver2(), PROJECT_ID);
		projectSamplesPage.toggleSelectAll();
		projectSamplesPage.removeSamples();

		//manager tries to complete metadata import
		page.clickUploadButton();
		assertTrue(page.hasTableErrors(), "Table errors did not display");
	}

	@Test
	public void testFailedUploadByCreatingNewSample() {
		//manager starts a metadata import
		ProjectSampleMetadataImportPage page = ProjectSampleMetadataImportPage.goToPage(driver());
		page.uploadMetadataFile(GOOD_FILE_PATH);
		page.selectSampleNameColumn(SAMPLE_NAME_COLUMN);
		page.goToReviewPage();

		//admin creates new sample
		ProjectSamplesPage projectSamplesPage = ProjectSamplesPage.goToPage(driver2(), PROJECT_ID);
		projectSamplesPage.openCreateNewSampleModal();
		projectSamplesPage.enterSampleName("sample5");
		projectSamplesPage.clickOk();

		//manager tries to complete metadata import
		page.clickUploadButton();
		assertTrue(page.hasTableErrors(), "Table errors did not display");
	}

	@Test
	public void testFailedUploadByRemovingPrivileges() {
		//manager starts a metadata import
		ProjectSampleMetadataImportPage page = ProjectSampleMetadataImportPage.goToPage(driver());
		page.uploadMetadataFile(GOOD_FILE_PATH);
		page.selectSampleNameColumn(SAMPLE_NAME_COLUMN);
		page.goToReviewPage();

		//admin removes manager from project
		ProjectMembersPage projectMembersPage = ProjectMembersPage.goTo(driver2());
		projectMembersPage.removeManager(1);

		//manager tries to complete metadata import
		page.clickUploadButton();
		assertTrue(page.isErrorNotificationDisplayed(), "Error notification did not display");
	}

	@Test
	public void testUploadDoesNotOverwriteExistingSampleMetadata() {
		ProjectSampleMetadataImportPage page = ProjectSampleMetadataImportPage.goToPage(driver());
		page.uploadMetadataFile(GOOD_FILE_PATH);
		page.selectSampleNameColumn(SAMPLE_NAME_COLUMN);
		page.goToReviewPage();
		page.goToCompletePage();
		assertTrue(page.isSuccessDisplayed(), "Successful upload did not happen");
		ProjectSamplesPage samplePage = ProjectSamplesPage.goToPage(driver(), PROJECT_ID);
		samplePage.clickSampleName(SAMPLE_NAME);
		SampleDetailsViewer sampleDetailsViewer = SampleDetailsViewer.getSampleDetails(driver());
		sampleDetailsViewer.clickMetadataTabLink();
		assertTrue(sampleDetailsViewer.addNewMetadataButtonVisible());
		sampleDetailsViewer.getNumberOfMetadataEntries();
		assertEquals(10, sampleDetailsViewer.getNumberOfMetadataEntries(),
				"Should have the proper number of metadata entries");
		assertEquals("Sneezing", sampleDetailsViewer.getValueForMetadataField("symptom"),
				"Should have existing metadata");
		assertEquals("AB", sampleDetailsViewer.getValueForMetadataField("Province"), "Should have uploaded metadata");
	}
}
