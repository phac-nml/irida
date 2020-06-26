package ca.corefacility.bioinformatics.irida.ria.integration.samples;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.samples.SampleFilesPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * <p> Integration test to ensure that the Sample Details Page. </p>
 *
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/samples/SamplePagesIT.xml")
public class SampleFilesPageIT extends AbstractIridaUIITChromeDriver {
	private final String SAMPLE_LABEL = "sample1";
	private final Long SAMPLE_ID = 1L;

	private final String FASTQ_FILE = "src/test/resources/files/test_file.fastq";
	private final String FASTA_FILE = "src/test/resources/files/test_file.fasta";

	private SampleFilesPage page;

	private final List<Map<String, String>> BREADCRUMBS = ImmutableList.of(
			ImmutableMap.of(
					"href", "/samples",
					"text", "Samples"
			),
			ImmutableMap.of("href", "/samples/" + SAMPLE_ID, "text", "sample1")
	);

	@Before
	public void setUpTest() {
		LoginPage.loginAsManager(driver());
		page = new SampleFilesPage(driver());
	}

	@Test
	public void testPageSetup() {
		page.gotoPage(SAMPLE_ID);
		checkTranslations(page, ImmutableList.of("sample"), null);

		assertTrue("Page Title contains the sample label", page.getPageTitle().contains(SAMPLE_LABEL));
		assertEquals("Displays the correct number of sequence files", 4, page.getSequenceFileCount());
		assertEquals("Displays the correct number of assemblies", 2, page.getAssemblyFileCount());
		assertEquals("should be 1 qc entry", 1, page.getQcEntryCount());
		
		page.checkBreadCrumbs(BREADCRUMBS);
	}
	
	@Test
	public void testDeleteFile() {
		page.gotoPage(SAMPLE_ID);
		
		page.deleteFirstSequenceFile();
		assertTrue("Should display a confirmation message that the file was deleted",
				page.isDeleteConfirmationMessageDisplayed());
		assertEquals("Displays the correct number of sequence files", 3, page.getSequenceFileCount());
	}

	@Test
	public void testDeletePair(){
		page.gotoPage(SAMPLE_ID);
		
		page.deleteFirstSequenceFilePair();
		assertTrue("Should display a confirmation message that the file was deleted", page.isDeleteConfirmationMessageDisplayed());
		assertEquals("Displays the correct number of sequence files", 2, page.getSequenceFileCount());
	}
	
	@Test
	public void testDeleteAssembly() {
		page.gotoPage(SAMPLE_ID);
		
		page.deleteFirstAssemblyFile();
		assertTrue("Should display a confirmation message that the file was deleted",
				page.isDeleteConfirmationMessageDisplayed());
		assertEquals("Displays the correct number of assemblies", 1, page.getAssemblyFileCount());
		assertEquals("Should not have deleted sequence files (displays correct number of sequence files)", 4, page.getSequenceFileCount());
	}

	@Test
	public void testSequenceFilesUploads() {
		page.gotoPage(SAMPLE_ID);
		assertEquals("Displays the correct number of sequence files", 4, page.getSequenceFileCount());
		page.uploadSequenceFile(FASTQ_FILE);
		page.gotoPage(SAMPLE_ID);
		assertEquals("Displays the correct number of sequence files", 5, page.getSequenceFileCount());
		page.gotoPage(SAMPLE_ID);
		// Test wrong file format
		page.uploadSequenceFile(FASTA_FILE);
		assertTrue("Should display a warning if the wrong file type is being uploaded.", page.isFileTypeWarningDisplayed());
	}

	@Test
	public void testAssemblyUploads() {
		page.gotoPage(SAMPLE_ID);
		assertEquals("Displays the correct number of assemblies displayed", 2, page.getAssemblyFileCount());
		page.uploadAssemblyFile(FASTA_FILE);
		page.gotoPage(SAMPLE_ID);
		assertEquals("Displays the correct number of assemblies displayed", 3, page.getAssemblyFileCount());
		// Test wrong file format
		page.uploadAssemblyFile(FASTQ_FILE);
		assertTrue("Should display a warning if the wrong file type is being uploaded.", page.isFileTypeWarningDisplayed());
	}
	
	@Test
	public void testAccessMultiProjectSamplePage() {
		LoginPage.logout(driver());
		LoginPage.loginAsUser(driver());
		page = new SampleFilesPage(driver());
		page.gotoPage(5L);
		assertTrue("Page Title contains the sample label", page.getPageTitle().contains("sample5"));
	}
}
