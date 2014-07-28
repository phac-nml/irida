package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
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

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

/**
 * <p>
 * Integration test to ensure that the Project Details Page.
 * </p>
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

	public static final String GOOD_SAMPLE_NAME = "GoodSampleName";
	private WebDriver driver;
	private ProjectSamplesPage page;

	@Before
	public void setUp() {
		this.driver = new ChromeDriver();
		LoginPage loginPage = LoginPage.to(driver);
		loginPage.doLogin();
		this.page = new ProjectSamplesPage(driver);
	}

	@After
	public void destroy() {
		if (driver != null) {
			driver.close();
			driver.quit();
		}
	}

	@Test
	public void testPageSetUp() {
		page.goToPage();
		assertEquals("Project Samples has the correct title", "project Samples", page.getTitle());
		assertEquals("Displays all the samples", 5, page.getDisplayedSampleCount());
		assertEquals("No samples should be originally selected", 0, page.getSelectedSampleCount());
		assertEquals("Displays the ability to view files", 1, page.getCountOfSamplesWithFiles());
		page.clickSelectAllCheckbox();
		assertEquals("All samples should be selected", page.getDisplayedSampleCount(), page.getSelectedSampleCount());
		page.clickFirstSampleCheckbox();
		assertTrue("'selectAll' checkbox is in an indeterminate state", page.isSelectAllInIndeterminateState());
		page.clickFirstSampleCheckbox();
		assertTrue("'selectAll' should now be selected", page.isSelectAllSelected());

		// Show the files area for the first checkbox
		page.openFilesView();
		assertTrue("Should display the files area", page.isFilesAreaDisplayed());
	}

	/**
	 * Tests to see if clicking on the row header sorts the columns as expected.
	 */
	@Test
	public void testTableSort() {
		page.goToPage();
		assertTrue("Dates should be sorted in descending order originally", page.isAddedOnDateColumnSortedDesc());
		page.clickSampleNameHeader();
		assertTrue("Sample names are sorted ascending", page.isSampleNameColumnSortedAsc());
		page.clickSampleNameHeader();
		assertTrue("Samples should now be sorted descending", page.isSampleNameColumnSortedDesc());
	}

	@Test
	public void testAnotherSort() {
		page.goToPage();
		page.clickCreatedDateHeader();
		assertTrue("Added on date should be sorted ascending", page.isAddedOnDateColumnSortedAsc());
		page.clickCreatedDateHeader();
		assertTrue("Added on date should be sorted descending", page.isAddedOnDateColumnSortedDesc());
	}

	@Test
	public void testEditSampleName() {
		page.goToPage();
		page.clickEditFirstSample();
		assertTrue("Display a input to rename the sample", page.isRenameInputVisible());
		// Try to rename a sample correctly
		page.sendRenameSample(GOOD_SAMPLE_NAME);
		assertTrue("Success message shown", page.successMessageShown());
		assertEquals("Has renamed the sample", GOOD_SAMPLE_NAME, page.getSampleName());
	}

	@Test
	public void testEditSampleBadly() {
		page.goToPage();
		page.clickEditFirstSample();
		page.sendRenameSample("really bad name");
		assertFalse("Success message shown", page.successMessageShown());
		assertTrue("Has error field", page.hasErrorMessage());
		assertTrue("Shows correct message", page.hasFormattingMessage());

		// Cancel button should reset the state
		page.clickOnEditCancel();
		assertFalse("Error field should be gone", page.isRenameInputVisible());
	}

	@Test
	public void testDeleteProjectSample() {
		page.goToPage();
		int orig = page.getDisplayedSampleCount();
		assertTrue("Delete button should be disabled if no samples selected", page.isDeleteBtnDisabled());
		page.selectFirstSample();
		page.clickDeleteSamples();
		assertEquals("There should be one less sample displayed", orig - 1, page.getDisplayedSampleCount());
	}

	@Test
	public void testDeleteAllProjectSamples() {
		page.goToPage();
		assertEquals("Displays all the samples", 5, page.getDisplayedSampleCount());
		page.clickSelectAllCheckbox();
		page.clickDeleteSamples();
		assertEquals("Has one row - error row", 1, page.getDisplayedSampleCount());
		assertTrue("Shows the no samples message", page.isTableEmptyRowShown());
	}
}
