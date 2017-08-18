package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * <p>
 * Page Object to represent the projects page.
 * </p>
 *
 */
public class ProjectsPage extends AbstractPage {
    private static final Logger logger = LoggerFactory.getLogger(ProjectsPage.class);
	public static final String RELATIVE_URL = "projects";
	public static final String ADMIN_URL = RELATIVE_URL + "/all";

    public ProjectsPage(WebDriver driver) {
		super(driver);
	}

	public void toUserProjectsPage() {
		loadPage(RELATIVE_URL);
	}

	public void toAdminProjectsPage() {
		loadPage(ADMIN_URL);
	}

	private void loadPage(String url) {
		get(driver, url);
		waitForTime(100);
		waitForElementVisible(By.cssSelector("#projects tbody tr"));
	}

	public int projectsTableSize() {
		logger.trace("Getting table size");

		List<WebElement> projectList = driver.findElements(By.cssSelector("#projects tbody tr"));

		int size = projectList.size();

		// exclude the "no projects" row if it's empty
		if (size == 1) {
			WebElement next = projectList.iterator().next();
			if (next.findElement(By.cssSelector("td")).getAttribute("class").contains("dataTables_empty")) {
				logger.trace("Removing no projects found row");
				size--;
			}
		}

		return size;
	}

	public void gotoProjectPage(int row) {
		submitAndWait(driver.findElements(By.cssSelector("#projects .btn-link")).get(row));
	}

	public List<WebElement> getProjectColumn() {
		return driver.findElements(By.cssSelector("#projects tbody td:nth-child(2)"));
	}

	public void clickProjectNameHeader() {
		// Sorting row is the second one
		WebElement th = driver.findElement(By.cssSelector("[data-data='name']"));
		final String originalSortOrder = th.getAttribute("class");
		th.click();
		new WebDriverWait(driver, TIME_OUT_IN_SECONDS).until(
				(org.openqa.selenium.support.ui.ExpectedCondition<Boolean>) input -> {
					final String ariaSort = th.getAttribute("class");
					return ariaSort!= null && !ariaSort.equals(originalSortOrder);
				});
	}

	public void doSearch(String term) {
		WebElement input = driver.findElement(By.cssSelector("#projects_filter input"));
		input.clear();
		input.sendKeys(term);
		waitForElementInvisible(By.className("projects_processing"));
	}

	public void clickLinkToProject(int row) {
		List<WebElement> links = (List<WebElement>) waitForElementsVisible(By.cssSelector("#projects .btn-link"));
		submitAndWait(links.get(row));
	}
}
