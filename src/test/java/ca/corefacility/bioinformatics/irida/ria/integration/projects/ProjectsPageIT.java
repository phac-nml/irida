package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITPhantomJS;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.ProjectsPage;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * <p> Integration test to ensure that the Projects Page. </p>
 *
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class ProjectsPageIT extends AbstractIridaUIITPhantomJS {
	private ProjectsPage projectsPage;

	@Before
	public void setUpTest() {
		LoginPage.loginAsManager(driver());

		projectsPage = new ProjectsPage(driver());
		projectsPage.toUserProjectsPage();
	}

	@Test
	public void confirmTablePopulatedByProjects() {
		assertEquals("Projects table should be populated by 4 projects", 4, projectsPage.projectsTableSize());

		// Ensure buttons are created and direct to the write project.
		projectsPage.gotoProjectPage(1);
		assertTrue("Should be on specific project page", driver().getCurrentUrl().contains("/projects/2"));
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
