package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
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

	private WebDriver driver;
	private ProjectSamplesPage page;

	@Before
	public void setUp() {
		driver = BasePage.initializeChromeDriver();
		this.page = new ProjectSamplesPage(driver);
	}

	@After
	public void destroy() {
		BasePage.destroyDriver(driver);
	}

	@Test
	public void testInitialPageSetUp() {
		page.goTo();
		assertTrue(page.getTitle().contains("Samples"));
		assertEquals(10, page.getNumberOfSamplesDisplayed());
	}

	@Test
	public void testPaging() {
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
		assertEquals(5, page.getNumberOfSamplesDisplayed());
	}

	@Test
	public void testSelectSamples() {
		page.goTo();

		assertEquals(0, page.getNumberOfSamplesSelected());
		page.selectSampleByRow(0);
		page.selectSampleByRow(1);
		page.selectSampleByRow(2);
		assertEquals(3, page.getNumberOfSamplesSelected());
		page.selectSampleByRow(1);
		assertEquals(2, page.getNumberOfSamplesSelected());
	}
}
