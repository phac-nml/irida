package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import ca.corefacility.bioinformatics.irida.config.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.ProjectsPage;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * <p>
 * Integration test to ensure that the Projects Page.
 * </p>
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class ProjectsPageIT {
	private WebDriver driver;
	private ProjectsPage projectsPage;

	@Before
	public void setup() {
		driver = new ChromeDriver();
		LoginPage loginPage = LoginPage.to(driver);
		loginPage.doLogin();

		projectsPage = new ProjectsPage(driver);
	}

	@After
	public void destroy() {
		if (driver != null) {
			driver.close();
            driver.quit();
		}
	}

	@Test
	public void confirmTablePopulatedByProjects() {
		assertEquals("Projects table should be populated by 4 projects", 4, projectsPage.projectsTableSize());
	}

	@Test
	public void setsTheIconForCollaboratorsAndOwners() {
		WebElement collaboratorSpan = projectsPage.getCollaboratorSpan();
		assertTrue("Has the user icon", collaboratorSpan.getAttribute("class").contains("user"));

		WebElement ownerSpan = projectsPage.getOwnerSpan();
		assertTrue("Has the user icon", ownerSpan.getAttribute("class").contains("tower"));
	}

	@Test
	public void sortByName() {
		projectsPage.clickProjectNameHeader();
		List<WebElement> ascElements = projectsPage.getProjectColumn();
		assertTrue("Projects page is sorted Ascending", checkSortedAscending(ascElements));

		projectsPage.clickProjectNameHeader();
		List<WebElement> desElements = projectsPage.getProjectColumn();
		assertTrue("Projects page is sorted Descending", checkSortedDescending(desElements));
	}

	/**
	 * Checks if a List of {@link WebElement} is sorted in ascending order.
	 * 
	 * @param elements
	 *            List of {@link WebElement}
	 * @return if the list is sorted ascending
	 */
	private boolean checkSortedAscending(List<WebElement> elements) {
		boolean isSorted = true;
		for (int i = 1; i < elements.size(); i++) {
			if (elements.get(i).getText().compareTo(elements.get(i - 1).getText()) < 0) {
				isSorted = false;
				break;
			}
		}
		return isSorted;
	}

	/**
	 * Checks if a list of {@link WebElement} is sorted in descending order.
	 * 
	 * @param elements
	 *            List of {@link WebElement}
	 * @return if the list is sorted ascending
	 */
	private boolean checkSortedDescending(List<WebElement> elements) {
		boolean isSorted = true;
		for (int i = 1; i < elements.size(); i++) {
			if (elements.get(i).getText().compareTo(elements.get(i - 1).getText()) > 0) {
				isSorted = false;
				break;
			}
		}
		return isSorted;
	}
}
