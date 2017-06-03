package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.ProjectsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSamplesPage;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * <p> Integration test to ensure that the Projects Page. </p>
 *
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class ProjectsPageIT extends AbstractIridaUIITChromeDriver {
	private ProjectsPage projectsPage;

	@Before
	public void setUpTest() {
		LoginPage.loginAsManager(driver());

		projectsPage = new ProjectsPage(driver());
		projectsPage.toUserProjectsPage();
	}

	@Test
	public void confirmTablePopulatedByProjects() {
		assertEquals("Projects table should be populated by 7 projects", 7, projectsPage.projectsTableSize());

		// Ensure buttons are created and direct to the write project.
		projectsPage.gotoProjectPage(1);
		assertTrue("Should be on specific project page", driver().findElement(By.id("samplesTable")).isDisplayed());
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

	@Test
	public void testAdvancedFilters() {
		projectsPage.doSearch("coli");
		assertEquals("Projects table should be populated by 4 projects after applying filter", 4,
				projectsPage.projectsTableSize());

		// Lets clean that filter up with some searching
		projectsPage.doSearch("K-12");
		assertEquals("Projects table should be populated by 1 project after applying filter", 1,
				projectsPage.projectsTableSize());

		projectsPage.doSearch("3");
		assertEquals("Projects table should be populated by 1 projects after applying filter", 1, projectsPage.projectsTableSize());
	}

	@Test
	public void testLinkingToSpecificProject() {
		projectsPage.clickLinkToProject(0);
		ProjectSamplesPage page = ProjectSamplesPage.initPage(driver());
		assertEquals("Should link to the project page.", page.getActivePage(), "Samples");
	}

	/**
	 * Checks if a List of {@link WebElement} is sorted in ascending order.
	 *
	 * @param elements
	 * 		List of {@link WebElement}
	 *
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
	 * 		List of {@link WebElement}
	 *
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
