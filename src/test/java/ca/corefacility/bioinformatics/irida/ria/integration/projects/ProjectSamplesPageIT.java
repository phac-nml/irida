package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.Dimension;
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
import ca.corefacility.bioinformatics.irida.ria.integration.pages.BasePage;
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

	private WebDriver driver;
	private ProjectSamplesPage page;

	@Before
	public void setUp() {
		driver = new ChromeDriver();
		driver.manage().window().setSize(new Dimension(1024, 900));
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		LoginPage.login(driver, LoginPage.ADMIN_USERNAME, LoginPage.GOOD_PASSWORD);
		this.page = new ProjectSamplesPage(driver);
	}

	@After
	public void destroy() {
		BasePage.destroyDriver(driver);
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

    @Test
    public void testMergeProjectSamples() {
        page.goToPage();
        int origCount = page.getDisplayedSampleCount();
        assertEquals("Starts with the correct number of samples", 5, origCount);
        assertFalse("Combine samples modal should not be open.", page.isCombineSamplesModalOpen());
        page.clickFirstThreeCheckboxes();
        page.clickCombineSamples();
        assertTrue("Combine samples modal should be open", page.isCombineSamplesModalOpen());
        page.selectTheMergedSampleName("sample5");
        assertEquals("Now only has 3 samples since 3 were merged", 3, page.getDisplayedSampleCount());
        assertEquals("Sample has the correct name", "sample5", page.getSampleNameForRow(1));
    }

    @Test
    public void testMergeProjectSamplesNewName() {
        String newName = "FredPenner";
        page.goToPage();
        int origCount = page.getDisplayedSampleCount();
        assertEquals("Starts with the correct number of samples", 5, origCount);
        assertFalse("Combine samples modal should not be open.", page.isCombineSamplesModalOpen());
        page.clickFirstThreeCheckboxes();
        page.clickCombineSamples();
        assertTrue("Combine samples modal should be open", page.isCombineSamplesModalOpen());
        page.selectTheMergedSampleName(newName);
        assertEquals("Now only has 3 samples since 3 were merged", 3, page.getDisplayedSampleCount());
        assertEquals("Sample has the correct name", newName, page.getSampleNameForRow(1));
    }

    @Test
    public void testMergeProjectSamplesBadName() {
        String newName = "Fred Penner";
        page.goToPage();
        int origCount = page.getDisplayedSampleCount();
        assertEquals("Starts with the correct number of samples", 5, origCount);
        assertFalse("Combine samples modal should not be open.", page.isCombineSamplesModalOpen());
        page.clickFirstThreeCheckboxes();
        page.clickCombineSamples();
        assertTrue("Combine samples modal should be open", page.isCombineSamplesModalOpen());
        page.selectTheMergedSampleName(newName);
	    // TODO: This is the test that causes phantomjs to fail
        assertTrue("Displays merge error", page.isSampleMergeErrorDisplayed());
    }

	@Test
	public void testDisplaySampleFiles() {
		page.goToPage();
		page.showFilesView();
		assertTrue("Files view should be open", page.isFilesViewOpen());
		assertEquals("There should be three files displayed", 3, page.getDisplayedFilesCount());
	}

    @Test
    public void testCopySamples(){
    	page.goToPage();
    	page.clickFirstThreeCheckboxes();
    	page.copySamples("2");
    	assertTrue(page.successMessageShown());
    }

	@Test
	public void testTriStateCheckboxes(){
		page.goToPage();
		page.showFilesView();
		page.clickOnFileCheckBox(0);
		assertTrue("Files master checkbox should be indeterminate", page.isFilesViewControllerIndeterminate());
		assertTrue("Select all should be in indeterminate state", page.isSelectAllInIndeterminateState());
		page.clickOnFileCheckBox(1);
		assertTrue("Files master checkbox should be indeterminate", page.isFilesViewControllerIndeterminate());
		assertTrue("Select all should be in indeterminate state", page.isSelectAllInIndeterminateState());
		page.clickOnFileCheckBox(2);
		assertFalse("Files master checkbox should not be indeterminate", page.isFilesViewControllerSelected());
		assertTrue("Select all should be in indeterminate state", page.isSelectAllInIndeterminateState());
		assertFalse("Select all should not be selected", page.isSelectAllSelected());

		// Un-check all
		page.clickOnFileCheckBox(0);
		assertTrue("Files master checkbox should be indeterminate", page.isFilesViewControllerIndeterminate());
		assertTrue("Select all should be in indeterminate state", page.isSelectAllInIndeterminateState());
		page.clickOnFileCheckBox(1);
		assertTrue("Files master checkbox should be indeterminate", page.isFilesViewControllerIndeterminate());
		assertTrue("Select all should be in indeterminate state", page.isSelectAllInIndeterminateState());
		page.clickOnFileCheckBox(2);
		assertFalse("Files master checkbox should not be indeterminate", page.isFilesViewControllerSelected());
		assertFalse("Select all should be in indeterminate state", page.isSelectAllInIndeterminateState());
		assertFalse("Select all should not be selected", page.isSelectAllSelected());

		// IF the select all is selected all files open should be selected
		page.clickSelectAllCheckbox();
		assertTrue("All files should be selected when select all is selected", page.areAllFilesSelected());
		page.clickOnFileCheckBox(0);
		assertTrue("Select all should be indeterminate if one file is not selected",
				page.isSelectAllInIndeterminateState());
		assertTrue("Checkbox controlling the files view should be indeterminate if one file is not selected",
				page.isFilesViewControllerIndeterminate());
		page.clickOnFileCheckBox(0);
		assertFalse("Select all should be indeterminate if one file is not selected",
				page.isSelectAllInIndeterminateState());
		assertFalse("Checkbox controlling the files view should be indeterminate if one file is not selected",
				page.isFilesViewControllerIndeterminate());

		// Test to make sure the correct files are selected when opening and closing the file-details view
		page.goToPage();
		page.showFilesView();
		page.clickOnFileCheckBox(2);
		page.clickOnFileCheckBox(1);
		assertTrue("Should have files 1 and 2 selected", page.ensureCorrectFilesSelected(new int[] { 1, 2 }));
		page.hideFilesView();
		page.showFilesView();
		assertTrue("Should have files 1 and 2 selected", page.ensureCorrectFilesSelected(new int[] { 1, 2 }));
	}

	@Test
	public void testRunPipeline() {
		page.goToPage();
		page.clickFirstThreeCheckboxes();
		page.clickRunPipelineButton();
		assertTrue("Modal dialogue should be open.", page.isSelectPipelineModalOpen());
		page.clickOffSelectPipelineModal();
		assertTrue("Modal dialogue should be open.", page.isSelectPipelineModalClosed());
	}
}
