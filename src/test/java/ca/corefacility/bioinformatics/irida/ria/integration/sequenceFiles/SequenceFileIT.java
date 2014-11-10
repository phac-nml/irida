package ca.corefacility.bioinformatics.irida.ria.integration.sequenceFiles;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.sequenceFiles.SequenceFilePages;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.TestUtilities;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

/**
 * Created by josh on 14-08-06.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/sequenceFiles/SequenceFileView.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class SequenceFileIT {
	private static final Logger logger = LoggerFactory.getLogger(SequenceFileIT.class);
	/*
	 * FILE ATTRIBUTES
	 */
	private static final String FILE_NAME = "test_file.fastq";
	private static final String FILE_ID = "1";
	private static final String FILE_ENCODING = "Sanger / Illumina 1.9";
	private static final String FILE_CREATED = "Jul 18, 2013";
	private static final String FILE_TOTAL_SEQUENCE = "4";
	private static final String FILE_TOTAL_BASES = "937";
	private static final String FILE_MIN_LENGTH = "184";
	private static final String FILE_MAX_LENGTH = "251";
	private static final String FILE_GC_CONTENT = "30";
	private WebDriver driver;
	private SequenceFilePages page;

	@Before
	public void setUp() {
		this.driver = TestUtilities.setDriverDefaults(new ChromeDriver());
		LoginPage.loginAsAdmin(driver);
		page = new SequenceFilePages(driver);
	}

	@Test
	public void testSequenceFileDetailsPage() {
		logger.debug("Testing the Sequence File Details / Chart Page");
		page.goToDetailsPage();
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

	@After
	public void destroy() {
		if (driver != null) {
			driver.close();
			driver.quit();
		}
	}
}
