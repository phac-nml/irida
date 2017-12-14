package ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 */
public class AnalysesUserPage extends AbstractPage {
	@FindBy(id = "clearFilterBtn")
	private WebElement filterClear;

	@FindBy(className = "analysis__state")
	private List<WebElement> analysesList;

	@FindBy(className = "download-btn")
	private List<WebElement> downloadAnalysisBtn;
	
	@FindBy(className = "remove-btn")
	private List<WebElement> deleteAnalysisBtn;

	@FindBy(className = "progress-bar")
	private List<WebElement> progressBars;

	public AnalysesUserPage(WebDriver driver) {
		super(driver);
	}

	public static AnalysesUserPage initializePage(WebDriver driver) {
		get(driver, "analysis");
		return PageFactory.initElements(driver, AnalysesUserPage.class);
	}

	public static AnalysesUserPage initializeAdminPage(WebDriver driver) {
		get(driver, "analysis/all");
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("table tbody tr")));
		return PageFactory.initElements(driver, AnalysesUserPage.class);
	}
	
	public static AnalysesUserPage initializeProjectPage(Long projectId, WebDriver driver) {
		get(driver, "projects/" + projectId + "/analyses");
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("table tbody tr")));
		return PageFactory.initElements(driver, AnalysesUserPage.class);
	}
	
	public void deleteFirstAnalysis() {
		deleteAnalysisBtn.iterator().next().click();
		WebElement deleteButton = waitForElementToBeClickable(driver.findElement(By.id("delete-analysis-button")));
		deleteButton.click();
		waitForJQueryAjaxResponse();
	}

	public void clearFilter() {
		filterClear.click();
		waitForTime(100);
	}

	public void filterByName(String name) {
		// open filtering model
		WebElement openModal = waitForElementToBeClickable(driver.findElement(By.id("openFilterModal")));
		openModal.click();

		WebElement filterName = waitForElementVisible(By.id("nameFilter"));

		filterName.clear();
		filterName.sendKeys(name);

		driver.findElement(By.id("filterAnalysesBtn")).click();

		waitForTime(400);
	}

	public void filterByState(String text) {
		// open filtering model
		WebElement openModal = waitForElementToBeClickable(driver.findElement(By.id("openFilterModal")));
		openModal.click();

		WebElement filterState = waitForElementVisible(By.id("analysisStateFilter"));

		Select state = new Select(filterState);
		state.selectByVisibleText(text);

		driver.findElement(By.id("filterAnalysesBtn")).click();

		waitForTime(400);
	}

	public void filterByType(String text) {
		WebElement openModal = waitForElementToBeClickable(driver.findElement(By.id("openFilterModal")));
		openModal.click();

		WebElement filterType = waitForElementVisible(By.id("workflowIdFilter"));
		Select type = new Select(filterType);
		type.selectByVisibleText(text);

		driver.findElement(By.id("filterAnalysesBtn")).click();

		waitForTime(400);
	}

	public int getNumberOfProgressBars() {
		return progressBars.size();
	}

	public String getPercentComplete(int row) {
		return progressBars.get(row).getAttribute("aria-valuenow");
	}

	public int getNumberOfAnalyses() {
		return analysesList.size();
	}

	public int getNumberOfDownloadBtns() {
		int count = 0;
		for (WebElement btn : downloadAnalysisBtn) {
			if (btn.isDisplayed()) {
				count++;
			}
		}
		return count;
	}
}
