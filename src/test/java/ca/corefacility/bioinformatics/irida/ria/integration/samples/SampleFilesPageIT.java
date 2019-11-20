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
	private SampleFilesPage page;

	private List<Map<String, String>> BREADCRUMBS = ImmutableList.of(
			ImmutableMap.of(
					"href", "/samples",
					"text", "Samples"
			),
			ImmutableMap.of(
					"href", "/samples/" + SAMPLE_ID,
					"text", String.valueOf(SAMPLE_ID)
			),
			ImmutableMap.of(
					"href", "/samples/" + SAMPLE_ID + "/sequenceFiles",
					"text", "Sequence Files"
			)
	);

	@Before
	public void setUpTest() {
		LoginPage.loginAsManager(driver());
		page = new SampleFilesPage(driver());
	}

	@Test
	public void testPageSetup() {
		page.gotoPage(SAMPLE_ID);
		checkTranslations(page, ImmutableList.of("sample-files"), null);

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
	public void testGoodFileUploads() throws InterruptedException {
		page.gotoPage(SAMPLE_ID);
		page.selectGoodFastqFiles();
		assertTrue("Should display progress bar for file uploads", page.isProgressBarDisplayed());
		
		/*
		 * TODO: Modify this test to check for the file successfully uploading
		 * Note: This sleep is to allow the upload and file processing to
		 * complete before dbunit tries to clear the database
		 */
		Thread.sleep(5000);
	}

	@Test
	public void testBadFileUploads() throws InterruptedException {
		page.gotoPage(SAMPLE_ID);
		page.selectBadFastaFile();
		assertTrue("Should display a warning if the wrong file type is being uploaded.", page.isFileTypeWarningDisplayed());
		
		/*
		 * TODO: Modify this test to check for the file upload stateNote: This
		 * sleep is to allow the upload and file processing to complete before
		 * dbunit tries to clear the database
		 */
		Thread.sleep(5000);
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
