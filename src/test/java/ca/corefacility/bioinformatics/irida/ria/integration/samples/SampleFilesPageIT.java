package ca.corefacility.bioinformatics.irida.ria.integration.samples;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.samples.SampleFilesPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p>
 * Integration test to ensure that the Sample Details Page.
 * </p>
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/samples/SamplePagesIT.xml")
public class SampleFilesPageIT extends AbstractIridaUIITChromeDriver {
	private final String SAMPLE_LABEL = "sample1";
	private final Long SAMPLE_ID = 1L;

	private final String FASTQ_FILE = "src/test/resources/files/test_file.fastq";
	private final String FASTA_FILE = "src/test/resources/files/test_file.fasta";

	private SampleFilesPage page;

	private final List<Map<String, String>> BREADCRUMBS = ImmutableList.of(
			ImmutableMap.of("href", "/samples", "text", "Samples"),
			ImmutableMap.of("href", "/samples/" + SAMPLE_ID, "text", "sample1"));

	@BeforeEach
	public void setUpTest() {
		LoginPage.loginAsManager(driver());
		page = new SampleFilesPage(driver());
	}

	@Test
	public void testPageSetup() {
		page.gotoPage(SAMPLE_ID);
		checkTranslations(page, ImmutableList.of("sample"), null);

		assertTrue(page.getPageTitle().contains(SAMPLE_LABEL), "Page Title contains the sample label");
		assertEquals(4, page.getSequenceFileCount(), "Displays the correct number of sequence files");
		assertEquals(2, page.getAssemblyFileCount(), "Displays the correct number of assemblies");
		assertEquals(1, page.getQcEntryCount(), "should be 1 qc entry");

		page.checkBreadCrumbs(BREADCRUMBS);
	}

	@Test
	public void testDeleteFile() {
		page.gotoPage(SAMPLE_ID);

		page.deleteFirstSequenceFile();
		assertTrue(page.isDeleteConfirmationMessageDisplayed(),
				"Should display a confirmation message that the file was deleted");
		assertEquals(3, page.getSequenceFileCount(), "Displays the correct number of sequence files");
	}

	@Test
	public void testDeletePair() {
		page.gotoPage(SAMPLE_ID);

		page.deleteFirstSequenceFilePair();
		assertTrue(page.isDeleteConfirmationMessageDisplayed(),
				"Should display a confirmation message that the file was deleted");
		assertEquals(2, page.getSequenceFileCount(), "Displays the correct number of sequence files");
	}

	@Test
	public void testDeleteAssembly() {
		page.gotoPage(SAMPLE_ID);

		page.deleteFirstAssemblyFile();
		assertTrue(page.isDeleteConfirmationMessageDisplayed(),
				"Should display a confirmation message that the file was deleted");
		assertEquals(1, page.getAssemblyFileCount(), "Displays the correct number of assemblies");
		assertEquals(4, page.getSequenceFileCount(),
				"Should not have deleted sequence files (displays correct number of sequence files)");
	}

	@Test
	public void testSequenceFilesUploads() {
		page.gotoPage(SAMPLE_ID);
		assertEquals(4, page.getSequenceFileCount(), "Displays the correct number of sequence files");
		page.uploadSequenceFile(FASTQ_FILE);
		page.gotoPage(SAMPLE_ID);
		assertEquals(5, page.getSequenceFileCount(), "Displays the correct number of sequence files");
		page.gotoPage(SAMPLE_ID);
		// Test wrong file format
		page.uploadSequenceFile(FASTA_FILE);
		assertTrue(page.isFileTypeWarningDisplayed(),
				"Should display a warning if the wrong file type is being uploaded.");
	}

	@Test
	public void testAssemblyUploads() {
		page.gotoPage(SAMPLE_ID);
		assertEquals(2, page.getAssemblyFileCount(), "Displays the correct number of assemblies displayed");
		page.uploadAssemblyFile(FASTA_FILE);
		page.gotoPage(SAMPLE_ID);
		assertEquals(3, page.getAssemblyFileCount(), "Displays the correct number of assemblies displayed");
		// Test wrong file format
		page.uploadAssemblyFile(FASTQ_FILE);
		assertTrue(page.isFileTypeWarningDisplayed(),
				"Should display a warning if the wrong file type is being uploaded.");
	}

	@Test
	public void testAccessMultiProjectSamplePage() {
		LoginPage.logout(driver());
		LoginPage.loginAsUser(driver());
		page = new SampleFilesPage(driver());
		page.gotoPage(5L);
		assertTrue(page.getPageTitle().contains("sample5"), "Page Title contains the sample label");
	}

	@Test
	public void testCorrectYear() {
		page.gotoPage(SAMPLE_ID);
		assertEquals("31 Dec 2013", page.getSequenceFileCreatedDate("03-3333_S1_L001_R1_001.fastq"),
				"Year should be 2013");
	}
}
