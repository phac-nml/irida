package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		waitForElementVisible(By.cssSelector("#projectsTable tbody tr"));
	}

	public int projectsTableSize() {
		logger.trace("Getting table size");

		List<WebElement> projectList = driver.findElements(By.cssSelector("#projectsTable tbody tr"));

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
		submitAndWait(driver.findElements(By.cssSelector("#projectsTable .item-link")).get(row));
	}

	public List<WebElement> getProjectColumn() {
		return driver.findElements(By.cssSelector("#projectsTable tbody td:nth-child(2)"));
	}

	public void clickProjectNameHeader() {
		// Sorting row is the second one
		WebElement th = driver.findElements(By.cssSelector("#projectsTable th")).get(1);
		final String originalSortOrder = th.getAttribute("aria-sort");
		th.findElement(By.className("header-name")).click();
		new WebDriverWait(driver, TIME_OUT_IN_SECONDS).until(
				(org.openqa.selenium.support.ui.ExpectedCondition<Boolean>) input -> {
					final String ariaSort = th.getAttribute("aria-sort");
					return ariaSort!= null && !ariaSort.equals(originalSortOrder);
				});
	}

	public void filterByName(String name) {
		openFilters();
		WebElement input = waitForElementVisible(By.id("nameFilter"));
		input.sendKeys(name);
		submitFilter("nameFilterClear");
	}

	public void filterByOrganism(String organism) {
		openFilters();
		WebElement input = driver.findElement(By.id("organismFilter"));
		input.sendKeys(organism);
		submitFilter("organismFilterClear");
	}

	private void openFilters() {
		WebElement btn = waitForElementVisible(By.id("openFilterModal"));
		btn.click();
	}

	private void submitFilter(String filterClearerId) {
		driver.findElement(By.id("filterProjectsBtn")).click();
		waitForElementInvisible(By.className("modal-header"));
	}

	public void doSearch(String term) {
		driver.findElement(By.cssSelector("#projectsTable_filter input")).sendKeys(term);
		waitForElementInvisible(By.className("projectsTable_processing"));
	}

	public void clickLinkToProject(int row) {
		List<WebElement> links = (List<WebElement>) waitForElementsVisible(By.className("item-link"));
		submitAndWait(links.get(row));
	}
}
