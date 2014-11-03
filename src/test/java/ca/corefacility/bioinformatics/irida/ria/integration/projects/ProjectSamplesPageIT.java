package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
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
import ca.corefacility.bioinformatics.irida.ria.integration.pages.BasePage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSamplesPage;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableList;

/**
 * <p> Integration test to ensure that the Project Details Page. </p>
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/ProjectSamplesView.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class ProjectSamplesPageIT {
	private static final Logger logger = LoggerFactory.getLogger(ProjectSamplesPageIT.class);

	private WebDriver driver;
	private ProjectSamplesPage page;

	@Before
	public void setUp() {
		driver = BasePage.initializeDriver();
		this.page = new ProjectSamplesPage(driver);
	}

	@After
	public void destroy() {
		BasePage.destroyDriver(driver);
	}

	@Test
	public void testInitialPageSetUp() {
		logger.info("Testing page set up for: Project Samples");
		page.goTo();
		assertTrue(page.getTitle().contains("Samples"));
		assertEquals(10, page.getNumberOfSamplesDisplayed());
	}

	@Test
	public void testPaging() {
		logger.info("Testing paging for: Project Samples");
		page.goTo();

		// Initial setup
		assertFalse(page.isFirstButtonEnabled());
		assertFalse(page.isPreviousButtonEnabled());
		assertTrue(page.isNextButtonEnabled());
		assertTrue(page.isLastButtonEnabled());
		assertEquals(1, page.getGetSelectedPageNumber());

		// Second Page
		page.selectPage(2);
		assertEquals(2, page.getGetSelectedPageNumber());
		assertTrue(page.isFirstButtonEnabled());
		assertTrue(page.isPreviousButtonEnabled());
		assertTrue(page.isNextButtonEnabled());
		assertTrue(page.isLastButtonEnabled());
		assertEquals(10, page.getNumberOfSamplesDisplayed());

		// Third Page (1 element)
		page.selectPage(3);
		assertTrue(page.isFirstButtonEnabled());
		assertTrue(page.isPreviousButtonEnabled());
		assertFalse(page.isNextButtonEnabled());
		assertFalse(page.isLastButtonEnabled());
		assertEquals(3, page.getGetSelectedPageNumber());
		assertEquals(1, page.getNumberOfSamplesDisplayed());

		// Previous Button
		page.clickPreviousPageButton();
		assertEquals(2, page.getGetSelectedPageNumber());
		page.clickPreviousPageButton();
		assertEquals(1, page.getGetSelectedPageNumber());

		// Next Button
		page.clickNextPageButton();
		assertEquals(2, page.getGetSelectedPageNumber());
		page.clickNextPageButton();
		assertEquals(3, page.getGetSelectedPageNumber());

		// First and List page buttons
		page.clickFirstPageButton();
		assertEquals(1, page.getGetSelectedPageNumber());
		assertFalse(page.isFirstButtonEnabled());
		page.clickLastPageButton();
		assertEquals(3, page.getGetSelectedPageNumber());
		assertFalse(page.isLastButtonEnabled());
		assertTrue(page.isFirstButtonEnabled());
		assertEquals(1, page.getNumberOfSamplesDisplayed());
	}

	@Test
	public void testSelectSamples() {
		logger.info("Testing selecting samples for: Project Samples");
		page.goTo();

		assertEquals(0, page.getNumberOfSamplesSelected());
		page.selectSampleByRow(0);
		page.selectSampleByRow(1);
		page.selectSampleByRow(2);
		assertEquals(3, page.getNumberOfSamplesSelected());
		page.selectSampleByRow(1);
		assertEquals(2, page.getNumberOfSamplesSelected());

		// If I go back to the page I expect them to be there
		page.goTo();
		assertEquals(2, page.getNumberOfSamplesSelected());
	}

	@Test
	public void testPagingWithSelectingSamples() {
		logger.info("Testing paging with selecting samples for: Project Samples");
		List<Integer> page1 = ImmutableList.of(0, 1, 6);
		page.goTo();

		assertEquals(0, page.getNumberOfSamplesSelected());
		page1.forEach(page::selectSampleByRow);
		assertEquals(3, page.getNumberOfSamplesSelected());
		assertTrue(page.isRowSelected(6));

		// Let's go to the second page
		page.clickNextPageButton();
		for (int row : page1) {
			assertFalse(page.isRowSelected(row));
		}
		assertEquals(0, page.getNumberOfSamplesSelected());
		page.selectSampleByRow(2);

		// Let's jump around a little
		jumpAroundLists();

		// Make sure samples are still selected on the first page
		page.clickFirstPageButton();
		for (int row : page1) {
			assertTrue(page.isRowSelected(row));
		}
		assertEquals(3, page.getNumberOfSamplesSelected());

		// Deselect first page samples
		page1.forEach(page::selectSampleByRow);
		assertEquals(0, page.getNumberOfSamplesSelected());

		jumpAroundLists();

		page.clickFirstPageButton();
		assertEquals(0, page.getNumberOfSamplesSelected());
	}

	@Test
	public void testFileSelection() {
		page.goTo();
		page.clickLastPageButton();
		assertFalse(page.isRowSelected(0));
		page.openFilesView(0);
		assertEquals(3, page.getNumberOfFiles());
		assertFalse(page.isFileSelected(0));
		page.selectFile(0);
		assertTrue(page.isFileSelected(0));
		assertTrue(page.isSampleIndeterminate(0));
		page.selectFile(1);
		assertTrue(page.isSampleIndeterminate(0));
		page.selectFile(2);
		assertTrue(page.isRowSelected(0));
	}

	@Test
	public void testSelectedSampleCount() {
		page.goTo();
		assertEquals(0, page.getTotalSelectedSamplesCount());
		page.selectSampleByRow(0);
		page.selectSampleByRow(1);
		assertEquals(2, page.getTotalSelectedSamplesCount());
		page.clickNextPageButton();
		assertEquals(2, page.getTotalSelectedSamplesCount());
		page.selectSampleByRow(5);
		assertEquals(3, page.getTotalSelectedSamplesCount());
		page.clickLastPageButton();
		assertEquals(3, page.getTotalSelectedSamplesCount());
		page.selectSampleByRow(0);
		assertEquals(4, page.getTotalSelectedSamplesCount());
		page.selectSampleByRow(0);
		assertEquals(3, page.getTotalSelectedSamplesCount());
		page.clickFirstPageButton();
		assertEquals(3, page.getTotalSelectedSamplesCount());
		page.selectSampleByRow(0);
		page.selectSampleByRow(1);
		assertEquals(1, page.getTotalSelectedSamplesCount());
		page.clickLastPageButton();
		assertEquals(1, page.getTotalSelectedSamplesCount());

		// What about when a file is selected.
		page.openFilesView(0);
		page.selectFile(1);
		assertEquals(2, page.getTotalSelectedSamplesCount());
		page.selectFile(1);
		assertEquals(1, page.getTotalSelectedSamplesCount());

	}

	@Test
	public void testDefaultMerge() {
		page.goTo();
		assertEquals(0, page.getTotalSelectedSamplesCount());
		assertFalse(page.isBtnEnabled("mergeBtn"));
		page.selectSampleByRow(0);
		page.selectSampleByRow(1);
		assertEquals(2, page.getTotalSelectedSamplesCount());
		assertTrue(page.isBtnEnabled("mergeBtn"));
		page.clickBtn("mergeBtn");
		assertTrue(page.isItemVisible("merge-samples-modal"));
		page.clickBtn("confirmMergeBtn");
		assertTrue(page.checkSuccessNotification());
		assertEquals(0, page.getTotalSelectedSamplesCount());
	}

	@Test
	public void testRenameMerge() {
		page.goTo();
		assertEquals(0, page.getTotalSelectedSamplesCount());
		assertFalse(page.isBtnEnabled("mergeBtn"));
		page.selectSampleByRow(0);
		page.selectSampleByRow(1);
		assertEquals(2, page.getTotalSelectedSamplesCount());
		page.clickBtn("mergeBtn");
		assertTrue(page.isItemVisible("merge-samples-modal"));

		// Try entering a name that is too short
		assertTrue(page.isBtnEnabled("confirmMergeBtn"));
		page.enterNewMergeSampleName("HI");
		assertTrue(page.isItemVisible("merge-length-error"));
		assertFalse(page.isBtnEnabled("confirmMergeBtn"));

		// Try entering a name with spaces
		page.enterNewMergeSampleName("HIBOB I AM WRONG");
		assertTrue(page.isItemVisible("merge-format-error"));
		assertFalse(page.isBtnEnabled("confirmMergeBtn"));

		// Try to enter a proper name name
		String oriName = page.getSampleNameByRow(0);
		String newLongName = "LONGERNAME";
		page.enterNewMergeSampleName(newLongName);
		assertFalse(page.isItemVisible("merge-length-error"));
		assertFalse(page.isItemVisible("merge-format-error"));
		assertTrue(page.isBtnEnabled("confirmMergeBtn"));
		page.clickBtn("confirmMergeBtn");
		assertTrue(page.checkSuccessNotification());
		String updatedName = page.getSampleNameByRow(0);
		assertFalse(oriName.equals(updatedName));
		assertTrue(updatedName.equals(newLongName));
	}

	private void jumpAroundLists() {
		page.clickFirstPageButton();
		page.clickLastPageButton();
		page.clickPreviousPageButton();
		page.clickPreviousPageButton();
		page.clickNextPageButton();
	}
}
