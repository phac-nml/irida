package ca.corefacility.bioinformatics.irida.ria.integration.sequenceFiles;

import java.io.IOException;
import java.nio.file.Path;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.sequenceFiles.SequenceFilePages;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.FileUtilities;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/sequenceFiles/SequenceFileView.xml")
@ActiveProfiles("it")
public class SequenceFilePageIT extends AbstractIridaUIITChromeDriver {
	private final FileUtilities fileUtilities = new FileUtilities();

	@Autowired
	@Qualifier("outputFileBaseDirectory")
	private Path outputFileBaseDirectory;

	private static final Logger logger = LoggerFactory.getLogger(SequenceFilePageIT.class);
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

	private SequenceFilePages page;

	@BeforeEach
	public void setUpTest() {
		LoginPage.loginAsManager(driver());
		page = new SequenceFilePages(driver());
	}

	@Test
	public void testSequenceFileFastQCChartsPage() {
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

		logger.debug("Testing the Sequence File FastQC Charts Page");
		page.goToChartsPage();
		assertTrue(page.isFastQCLinksVisible());
		assertFalse(page.isFastQCNoRunWarningDisplayed());
		assertEquals(3, page.getChartCount(), "Should display three charts");
	}

	@Test
	public void testSequenceFileOverrepresentedSequencePage() {
		logger.debug("Testing the Sequence File FastQC Overrepresented Sequences Page");
		page.goToOverrepresentedPage();
		assertEquals(1, page.getNumberOfOverrepresentedSequences(), "Should display 1 overrepresented sequence");
		assertTrue(page.getOverrepresentedSequence().matches("^[aAtTgGcC]+$"), "Should display a sequence");
		assertTrue(page.getOverrepresentedSequencePercentage().contains("%"),
				"Should display the percentage with a percent sign");
		assertEquals("1", page.getOverrepresentedSequenceCount(), "Should display the count");
		assertEquals("No Hit", page.getOverrepresentedSequenceSource(), "Should display the source");
	}

	@Test
	public void testSequenceFileFastQCDetailsPage() {
		logger.debug("Testing the Sequence File FastQC Details Page");
		page.goToDetailsPage();
		assertTrue(page.isFastQCLinksVisible());
		assertFalse(page.isFastQCNoRunWarningDisplayed());
		testPageChrome();
	}

	@Test
	public void testNoFastQCData() {
		page.goToDetailsPageWithNoData();
		assertEquals(page.getPageTitle(), "sequenceFile2");
		assertFalse(page.isFastQCLinksVisible());
		assertTrue(page.isFastQCNoRunWarningDisplayed());
	}

	private void testPageChrome() {
		logger.debug("Testing the Sequence File Overrepresented Sequence Page");
		assertEquals(FILE_NAME, page.getPageTitle(), "Has the file name as the page title");
		assertEquals(FILE_ID, page.getFileId(), "Display the file id");
		assertEquals(FILE_CREATED, page.getFileCreatedDate(), "Displays the file created date");
		assertEquals(FILE_ENCODING, page.getFileEncoding(), "Displays the file encoding");
		assertEquals(FILE_TOTAL_SEQUENCE, page.getTotalSequenceCount(), "Display the total sequence count");
		assertEquals(FILE_TOTAL_BASES, page.getTotalBasesCount(), "Display the total bases count");
		assertEquals(FILE_MIN_LENGTH, page.getMinLength(), "Displays the minLength");
		assertEquals(FILE_MAX_LENGTH, page.getMaxLength(), "Displays the maxLength");
		assertEquals(FILE_GC_CONTENT, page.getGCContent(), "Displays the gc content");
	}

}
