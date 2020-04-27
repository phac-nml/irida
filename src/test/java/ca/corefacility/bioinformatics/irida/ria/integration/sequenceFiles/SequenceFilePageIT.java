package ca.corefacility.bioinformatics.irida.ria.integration.sequenceFiles;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.sequenceFiles.SequenceFilePages;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/sequenceFiles/SequenceFileView.xml")
public class SequenceFilePageIT extends AbstractIridaUIITChromeDriver {
	private static final Logger logger = LoggerFactory.getLogger(SequenceFilePageIT.class);
	/*
	 * FILE ATTRIBUTES
	 */
	private static final String FILE_NAME = "test_file.fastq";
	private static final String FILE_ID = "1";
	private static final String FILE_ENCODING = "Sanger / Illumina 1.9";
	private static final String FILE_CREATED = "Jul. 18, 2013";
	private static final String FILE_TOTAL_SEQUENCE = "4";
	private static final String FILE_TOTAL_BASES = "937";
	private static final String FILE_MIN_LENGTH = "184";
	private static final String FILE_MAX_LENGTH = "251";
	private static final String FILE_GC_CONTENT = "30";

	private SequenceFilePages page;

	@Before
	public void setUpTest() {
		LoginPage.loginAsManager(driver());
		page = new SequenceFilePages(driver());
	}

	@Test
	public void testSequenceFileDetailsPage() {
		logger.debug("Testing the Sequence File Details / Chart Page");
		page.goToDetailsPage();
		assertTrue(page.isFastQCLinksVisible());
		assertTrue(page.isFastQCDetailsVisisble());
		assertFalse(page.isFastQCNoRunWarningDisplayed());
		testPageChrome();
		assertEquals("Should display three charts", 3, page.getChartCount());
	}

	@Test
	public void testSequenceFileOverrepresentedSequencePage() {
		page.goToOverrepresentedPage();
		testPageChrome();
		assertEquals("Should display 1 overrepresented sequence", 1, page.getNumberOfOverrepresentedSequences());
		assertTrue("Should display a sequence", page.getOverrepresentedSequence().matches("^[aAtTgGcC]+$"));
		assertTrue("Should display the percentage with a percent sign",
				page.getOverrepresentedSequencePercentage().contains("%"));
		assertEquals("Should display the count", "1", page.getOverrepresentedSequenceCount());
		assertEquals("Should display the source", "No Hit", page.getOverrepresentedSequenceSource());
	}

	@Test
	public void testNoFastQCData() {
		page.goToDetailsPageWithNoData();
		assertEquals("sequenceFile2", page.getPageTitle());
		assertFalse(page.isFastQCLinksVisible());
		assertFalse(page.isFastQCDetailsVisisble());
		assertTrue(page.isFastQCNoRunWarningDisplayed());
	}

	private void testPageChrome() {
		logger.debug("Testing the Sequence File Overrepresented Sequence Page");
		assertEquals("Has the file name as the page title", FILE_NAME, page.getPageTitle());
		assertEquals("Display the file id", FILE_ID, page.getFileId());
		assertEquals("Displays the file created date", FILE_CREATED, page.getFileCreatedDate());
		assertEquals("Displays the file encoding", FILE_ENCODING, page.getFileEncoding());
		assertEquals("Display the total sequence count", FILE_TOTAL_SEQUENCE, page.getTotalSequenceCount());
		assertEquals("Display the total bases count", FILE_TOTAL_BASES, page.getTotalBasesCount());
		assertEquals("Displays the minLength", FILE_MIN_LENGTH, page.getMinLength());
		assertEquals("Displays the maxLength", FILE_MAX_LENGTH, page.getMaxLength());
		assertEquals("Displays the gc content", FILE_GC_CONTENT, page.getGCContent());
	}

}
