package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.BasePage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.PageUtilities;

/**
 * <p> Page Object to represent the project samples page. </p>
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class ProjectSamplesPage {
	private static final String URL = BasePage.URL + "/projects/1/samples";
	private PageUtilities pageUtilities;
	private WebDriver driver;

	public ProjectSamplesPage(WebDriver driver) {
		this.driver = driver;
		this.pageUtilities = new PageUtilities(driver);
	}

	public void goTo() {
		driver.get(URL);
		pageUtilities.waitForElementVisible(By.className("sample-row"));
	}

	/**
	 * The the h1 heading for the page
	 *
	 * @return String value from within the h1 tag
	 */
	public String getTitle() {
		return driver.findElement(By.tagName("h1")).getText();
	}

	public int getNumberOfSamplesDisplayed() {
		return driver.findElements(By.className("sample-row")).size();
	}

	public int getGetSelectedPageNumber() {
		return Integer.parseInt(driver.findElement(By.cssSelector(".pagination li.active")).getText());
	}

	public void selectPage(int pageNum) {
		pageNum += 1; // 0 == First Button, 1 == Previous Button
		List<WebElement> links = driver.findElements(By.cssSelector(".pagination li"));
		links.get(pageNum).findElement(By.tagName("a")).click();
	}

	public void clickPreviousPageButton() {
		driver.findElements(By.cssSelector(".pagination li>a")).get(1).click();
		BasePage.waitForTime();
	}

	public void clickFirstPageButton() {
		driver.findElements(By.cssSelector(".pagination li>a")).get(0).click();
		BasePage.waitForTime();
	}

	public void clickNextPageButton() {
		List<WebElement> links = driver.findElements(By.cssSelector(".pagination li>a"));
		links.get(links.size() - 2).click();
		BasePage.waitForTime();
	}

	public void clickLastPageButton() {
		List<WebElement> links = driver.findElements(By.cssSelector(".pagination li>a"));
		links.get(links.size() - 1).click();
		BasePage.waitForTime();
	}

	public boolean isPreviousButtonEnabled() {
		return !driver.findElements(By.cssSelector(".pagination li")).get(1).getAttribute("class").contains("disabled");
	}

	public boolean isFirstButtonEnabled() {
		return !driver.findElements(By.cssSelector(".pagination li")).get(0).getAttribute("class").contains("disabled");
	}

	public boolean isNextButtonEnabled() {
		List<WebElement> links = driver.findElements(By.cssSelector(".pagination li"));
		return !links.get(links.size() - 2).getAttribute("class").contains("disabled");
	}

	public boolean isLastButtonEnabled() {
		List<WebElement> links = driver.findElements(By.cssSelector(".pagination li"));
		return !links.get(links.size() - 1).getAttribute("class").contains("disabled");
	}

	public int getNumberOfSamplesSelected() {
		return driver.findElements(By.cssSelector(".sample-select:checked")).size();
	}

	public void selectSampleByRow(int row) {
		List<WebElement> inputs = driver.findElements(By.className("sample-select"));
		inputs.get(row).click();
	}

	public boolean isRowSelected(int row) {
		List<WebElement> rows = driver.findElements(By.className("sample-row"));
		return rows.get(row).getAttribute("class").contains("selected");
	}

	public void openFilesView(int row) {
		WebElement sampleRow = driver.findElements(By.className("sample-row")).get(row);
		sampleRow.findElement(By.className("view-files")).click();
		pageUtilities.waitForElementVisible(By.className("details-row"));
	}

	public int getNumberOfFiles() {
		return driver.findElements(By.className("file-item")).size();
	}

	public void selectFile(int fileNum) {
		pageUtilities.waitForElementVisible(By.className("file-select"));
		List<WebElement> files = driver.findElements(By.className("file-select"));
		files.get(fileNum).click();
		BasePage.waitForTime();
	}

	public boolean isFileSelected(int fileNum) {
		List<WebElement> files = driver.findElements(By.className("file-item"));
		return files.get(fileNum).getAttribute("class").contains("selected");
	}

	public boolean isSampleIndeterminate(int row) {
		try {
			List<WebElement> inputs = driver.findElements(By.className("sample-select"));
			inputs.get(row).getAttribute("indeterminate");
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
