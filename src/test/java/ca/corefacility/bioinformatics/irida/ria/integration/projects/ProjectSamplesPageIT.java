package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSamplesPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.TestUtilities;

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
		driver = TestUtilities.setDriverDefaults(new ChromeDriver());
		this.page = new ProjectSamplesPage(driver);
	}

	@After
	public void destroy() {
		driver.quit();
	}

	@Test
	public void testInitialPageSetUp() {
		logger.info("Testing page set up for: Project Samples");
		LoginPage.loginAsAdmin(driver);
		page.goToPage();
		assertTrue(page.getTitle().contains("Samples"));
		assertEquals(10, page.getNumberOfSamplesDisplayed());
	}

	@Test
	public void testPaging() {
		logger.info("Testing paging for: Project Samples");
		LoginPage.loginAsAdmin(driver);
		page.goToPage();

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
		LoginPage.loginAsAdmin(driver);
		page.goToPage();

		assertEquals(0, page.getNumberOfSamplesSelected());
		selectFirstThreeSamples();
		assertEquals(3, page.getNumberOfSamplesSelected());
		page.selectSampleByRow(1);
		assertEquals(2, page.getNumberOfSamplesSelected());
	}

	@Test
	public void testPagingWithSelectingSamples() {
		logger.info("Testing paging with selecting samples for: Project Samples");
		List<Integer> page1 = ImmutableList.of(0, 1, 6);
		LoginPage.loginAsAdmin(driver);
		page.goToPage();

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
	public void testSelectedSampleCount() {
		LoginPage.loginAsAdmin(driver);
		page.goToPage();
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
	}

	@Test
	public void testDefaultMerge() {
		LoginPage.loginAsAdmin(driver);
		page.goToPage();
		assertEquals(0, page.getTotalSelectedSamplesCount());
		assertFalse(page.isBtnEnabled("samplesOptionsBtn"));
		page.selectSampleByRow(0);
		page.selectSampleByRow(1);
		assertEquals(2, page.getTotalSelectedSamplesCount());
		assertTrue(page.isBtnEnabled("samplesOptionsBtn"));
		page.clickBtn("samplesOptionsBtn");
		page.clickBtn("mergeBtn");
		assertTrue(page.isItemVisible("merge-samples-modal"));
		page.clickBtn("confirmMergeBtn");
		assertTrue(page.checkSuccessNotification());
		assertEquals(0, page.getTotalSelectedSamplesCount());
	}

	@Test
	public void testRenameMerge() {
		LoginPage.loginAsAdmin(driver);
		page.goToPage();
		assertEquals(0, page.getTotalSelectedSamplesCount());
		assertFalse(page.isBtnEnabled("samplesOptionsBtn"));
		page.selectSampleByRow(0);
		page.selectSampleByRow(1);
		assertEquals(2, page.getTotalSelectedSamplesCount());
		page.clickBtn("samplesOptionsBtn");
		page.clickBtn("mergeBtn");
		assertTrue(page.isItemVisible("merge-samples-modal"));

		// Try entering a name that is too short
		assertTrue(page.isBtnEnabled("confirmMergeBtn"));
		page.enterNewMergeSampleName("HI");
		assertTrue(page.isItemVisible("merge-length-error"));
		assertFalse(page.isBtnEnabled("confirmMergeBtn"));

		// Try entering a name with spaces
		page.enterNewMergeSampleName("HI BOB I AM WRONG");
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

	@Test
	public void testProjectUserCannotCopyOrMoveFilesToAnotherProject() {
		LoginPage.loginAsUser(driver);
		page.goToPage();
		assertFalse(page.isElementOnScreen("copyBtn"));
		assertFalse(page.isElementOnScreen("moveBtn"));
	}

	@Test
	public void testCopySamplesAsManagerToManagedProject() {
		LoginPage.login(driver, "project1Manager", "Password1");
		// Make sure the project to copy to is empty to begin with
		page.goToPage("2");
		assertEquals(0, page.getNumberOfSamplesDisplayed());

		page.goToPage();
		assertTrue(page.isElementOnScreen("copyBtn"));
		assertTrue(page.isElementOnScreen("moveBtn"));

		// Should be able to copy files to a project that they are a manager of.
		selectFirstThreeSamples();
		page.clickBtn("samplesOptionsBtn");
		page.clickBtn("copyBtn");
		assertTrue(page.isItemVisible("copy-samples-modal"));
		page.selectProjectByName("2", "confirm-copy-samples");
		assertTrue(page.isBtnEnabled("confirm-copy-samples"));
		page.clickBtn("confirm-copy-samples");
		page.checkSuccessNotification();

		// Check to make sure the samples where copied there
		page.goToPage("2");
		assertEquals(3, page.getNumberOfSamplesDisplayed());
	}

	@Test
	public void testMoveSamplesAsManagerToManagedProject() {
		LoginPage.login(driver, "project1Manager", "Password1");
		// Make sure the project to copy to is empty to begin with
		page.goToPage("2");
		assertEquals(0, page.getNumberOfSamplesDisplayed());
		page.goToPage();

		// Should be able to copy files to a project that they are a manager of.
		selectFirstThreeSamples();
		page.clickBtn("samplesOptionsBtn");
		page.clickBtn("moveBtn");
		assertTrue(page.isItemVisible("move-samples-modal"));
		page.selectProjectByName("2", "confirm-move-samples");
		assertTrue(page.isBtnEnabled("confirm-move-samples"));
		page.clickBtn("confirm-move-samples");
		page.checkSuccessNotification();

		// Check to make sure the samples where copied there
		page.goToPage("2");
		assertEquals(3, page.getNumberOfSamplesDisplayed());
	}

	@Test
	public void testCopySamplesAsManagerToUnmanagedProject() {
		LoginPage.login(driver, "project1Manager", "Password1");
		page.goToPage();
		assertFalse(page.isBtnEnabled("samplesOptionsBtn"));

		// Should be able to copy files to a project that they are a manager of.
		selectFirstThreeSamples();
		page.clickBtn("samplesOptionsBtn");
		page.clickBtn("copyBtn");
		assertTrue(page.isItemVisible("copy-samples-modal"));
		page.selectProjectByName("3", "confirm-copy-samples");
		assertFalse("Since the project does not exist in the list, they cannot copy files to it.",
				page.isBtnEnabled("confirm-copy-samples"));
	}

	@Test
	public void testAdminCopyFromAnyProjectToAnyProject() {
		LoginPage.loginAsAdmin(driver);
		page.goToPage();

		selectFirstThreeSamples();
		//Admin is not on project5
		page.clickBtn("samplesOptionsBtn");
		page.clickBtn("copyBtn");
		assertTrue(page.isItemVisible("copy-samples-modal"));
		page.selectProjectByName("5", "confirm-copy-samples");
		assertTrue(page.isBtnEnabled("confirm-copy-samples"));
		page.clickBtn("confirm-copy-samples");
		page.checkSuccessNotification();

		// Check to make sure the samples where copied there
		page.goToPage("5");
		assertEquals(3, page.getNumberOfSamplesDisplayed());
	}

	@Test
	public void testMultiSelection() {
		LoginPage.loginAsAdmin(driver);
		page.goToPage();

		// Test selecting a page
		assertEquals(0, page.getTotalNumberOfSamplesSelected());
		page.clickBtn("selectBtn");
		page.clickBtn("selectPageBtn");
		assertEquals(10, page.getTotalNumberOfSamplesSelected());

		// Test clearing the selections
		page.clickBtn("selectBtn");
		page.clickBtn("selectNoneBtn");
		assertEquals(0, page.getTotalNumberOfSamplesSelected());

		// Test select all
		page.clickBtn("selectBtn");
		page.clickBtn("selectAllBtn");
		assertEquals(21, page.getTotalNumberOfSamplesSelected());

		// Test clearing again
		page.clickBtn("selectBtn");
		page.clickBtn("selectNoneBtn");
		assertEquals(0, page.getTotalNumberOfSamplesSelected());

		// Select random samples on one page and then all on the second
		selectFirstThreeSamples();
		assertEquals(3, page.getTotalNumberOfSamplesSelected());
		page.clickNextPageButton();
		page.clickBtn("selectBtn");
		page.clickBtn("selectPageBtn");
		assertEquals(13, page.getTotalNumberOfSamplesSelected());
		page.clickBtn("selectBtn");
		page.clickBtn("selectAllBtn");
		assertEquals(21, page.getTotalNumberOfSamplesSelected());
	}

	@Test
	public void testExportLinker() {
		LoginPage.loginAsAdmin(driver);
		page.goToPage();

		assertFalse(page.isBtnEnabled("exportOptionsBtn"));
		page.selectSampleByRow(0);
		assertTrue(page.isBtnEnabled("exportOptionsBtn"));
		page.clickBtn("exportOptionsBtn");
		page.clickBtn("exportLinkerBtn");

		assertTrue(page.isItemVisible("linker-modal"));
		assertEquals(1, getSampleFlagCount(page.getLinkerScriptText()));
		page.clickBtn("linkerCloseBtn");

		// Select all samples
		page.clickBtn("selectBtn");
		page.clickBtn("selectAllBtn");
		page.clickBtn("exportOptionsBtn");
		page.clickBtn("exportLinkerBtn");
		assertEquals(0, getSampleFlagCount(page.getLinkerScriptText()));
		page.clickBtn("linkerCloseBtn");

		page.selectSampleByRow(0);
		int selectedCount = page.getTotalSelectedSamplesCount();
		page.clickBtn("exportOptionsBtn");
		page.clickBtn("exportLinkerBtn");
		String command = page.getLinkerScriptText();
		assertEquals(selectedCount, getSampleFlagCount(command));
	}

	private int getSampleFlagCount(String command) {
		Pattern pattern = Pattern.compile("-s");
		Matcher matcher = pattern.matcher(command);
		int count = 0;
		while (matcher.find()) {
			count++;
		}
		return count;
	}

	private void selectFirstThreeSamples() {
		page.selectSampleByRow(0);
		page.selectSampleByRow(1);
		page.selectSampleByRow(2);
	}

	private void jumpAroundLists() {
		page.clickFirstPageButton();
		page.clickLastPageButton();
		page.clickPreviousPageButton();
		page.clickPreviousPageButton();
		page.clickNextPageButton();
	}
}
