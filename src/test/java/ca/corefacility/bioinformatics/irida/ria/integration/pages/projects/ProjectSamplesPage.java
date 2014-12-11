package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.PageUtilities;

import com.google.common.collect.Ordering;

/**
 * <p> Page Object to represent the project samples page. </p>
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class ProjectSamplesPage extends AbstractPage {
	private static final Logger logger = LoggerFactory.getLogger(ProjectSamplesPage.class);
	private static final String RELATIVE_URL = "projects/1/samples";
	private static final String ALT_RELATIVE_URL = "projects/id/samples";
	private PageUtilities pageUtilities;

	public ProjectSamplesPage(WebDriver driver) {
		super(driver);
		this.pageUtilities = new PageUtilities(driver);
	}

	public void goToPage() {
		get(driver, RELATIVE_URL);
		waitForElementVisible(By.cssSelector("#samplesTable tbody"));
	}

	public void goToPage(String projectId) {
		get(driver, ALT_RELATIVE_URL.replace("id", projectId));
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
		List<WebElement> links = driver.findElements(By.cssSelector(".pagination li a"));
		// Remove directions links if there are any
		for (WebElement link : links) {
			if (link.getText().equals("1")) {
				break;
			}
			else {
				pageNum++;
			}
		}
		// Since paging has an offset of 1
		pageNum--;
		links.get(pageNum).click();
	}

	public void clickPreviousPageButton() {
		driver.findElements(By.cssSelector(".pagination li>a")).get(1).click();
	}

	public void clickFirstPageButton() {
		List<WebElement> links = driver.findElements(By.cssSelector(".pagination li a"));
		if (links.get(0).getText().equals("First")) {
			links.get(0).click();
		}
		// Remove directions links if there are any
		else {
			int count = 0;
			for (WebElement link : links) {
				count++;
				if (link.getText().equals("1")) {
					break;
				}
			}
			links.get(count).click();
		}
	}

	public void clickNextPageButton() {
		List<WebElement> links = driver.findElements(By.cssSelector(".pagination li>a"));
		links.get(links.size() - 2).click();
	}

	public void clickLastPageButton() {
		List<WebElement> links = driver.findElements(By.cssSelector(".pagination li>a"));
		links.get(links.size() - 1).click();
		waitForTime(700);
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
		return driver.findElements(By.cssSelector(".large-checkbox input[type=\"checkbox\"]:checked")).size();
	}

	public int getTotalNumberOfSamplesSelected() {
		return Integer.parseInt(driver.findElement(By.id("selected-count")).getText());
	}

	public void selectSampleByRow(int row) {
		List<WebElement> inputs = driver.findElements(By.className("large-checkbox"));
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

	public boolean isSampleIndeterminate(int row) {
		try {
			List<WebElement> inputs = driver.findElements(By.className("sample-select"));
			inputs.get(row).getAttribute("indeterminate");
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public boolean isBtnEnabled(String id) {
		return driver.findElement(By.id(id)).isEnabled();
	}

	public void clickBtn(String id) {
		driver.findElement(By.id(id)).click();
		waitForTime(700);
	}

	public boolean isItemVisible(String id) {
		try {
			return driver.findElement(By.id(id)).isDisplayed();
		} catch (Exception e) {
			logger.info("No element with id of: " + id);
			return false;
		}
	}

	public boolean checkSuccessNotification() {
		return pageUtilities.checkSuccessNotification();
	}

	public int getTotalSelectedSamplesCount() {
		return Integer.parseInt(driver.findElement(By.id("selected-count")).getText());
	}

	public void enterNewMergeSampleName(String name) {
		WebElement input = driver.findElement(By.id("newName"));
		input.clear();
		input.sendKeys(name);
		waitForTime(700);
	}

	public String getSampleNameByRow(int row) {
		List<WebElement> rows = driver.findElements(By.className("sample-row"));
		return rows.get(row).findElement(By.className("sample-name")).getText();
	}

	public void selectProjectByName(String name, String submitBtn) {
		WebElement input = openSelect2List(driver);
		input.sendKeys(name);
		waitForTime(600);
		input.sendKeys(Keys.ENTER);
	}

	public String getLinkerScriptText() {
		return driver.findElement(By.id("linkerCmd")).getText();
	}

	// Table sorting
	public void sortTableByName() {
		driver.findElement(By.id("sortName")).click();
	}
	public void sortTableByCreatedDate() { driver.findElement(By.id("sortCreatedDate")).click();}

	public boolean isTableSortedAscByCreationDate() {
		List<WebElement> elms = driver.findElements(By.className("createdDate"));
		List<String> dates = elms.stream().map(element -> element.getAttribute("data-date"))
				.collect(Collectors.toList());
		return Ordering.natural().isOrdered(dates);
	}

	public boolean isTableSortedAscBySampleName() {
		List<WebElement> elms = driver.findElements(By.cssSelector(".sample-name a"));
		List<String> names = elms.stream().map(WebElement::getText).collect(Collectors.toList());
		return Ordering.natural().isOrdered(names);
	}

	public boolean isTableSortedDescByCreationDate() {
		List<WebElement> elms = driver.findElements(By.className("createdDate"));
		List<String> dates = elms.stream().map(element -> element.getAttribute("data-date"))
				.collect(Collectors.toList());
		return Ordering.natural().reverse().isOrdered(dates);
	}

	public boolean isTableSortedDescBySampleName() {
		List<WebElement> elms = driver.findElements(By.className("sample-name"));
		List<String> names = elms.stream().map(element -> element.getText())
				.collect(Collectors.toList());
		return Ordering.natural().reverse().isOrdered(names);
	}

	// Filtering
	public int getTotalSampleCount() {
		return Integer.parseInt(driver.findElement(By.id("samples-total")).getText());
	}

	public int getFilteredSampleCount() {
		return Integer.parseInt(driver.findElement(By.id("samples-filtered")).getText());
	}

	public void filterByName(String name) {
		WebElement input = driver.findElement(By.id("sample-name-filter"));
		input.clear();
		input.sendKeys(name);
		waitForTime(550);
	}

	public void filterByOrganism(String organism) {
		WebElement input = driver.findElement(By.id("sample-organism-filter"));
		input.clear();
		input.sendKeys(organism);
		waitForTime(550);
	}

	// Galaxy
	public boolean isGalaxyModalOpen() {
		return driver.findElements(By.id("galaxy-modal")).size() > 0;
	}

	public void updateGalaxyEmail(String email) {
		WebElement emailField = driver.findElement(By.id("email"));
		emailField.clear();
		emailField.sendKeys(email);
		waitForTime(400);
	}

	public void updateGalaxyDataLibraryInput(String name) {
		WebElement library = driver.findElement(By.id("library"));
		library.clear();
		library.sendKeys(name);
		waitForTime(400);
	}

	public boolean isGalaxySubmitBtnEnabled () {
		return driver.findElement(By.id("galaxy-submit-btn")).isEnabled();
	}

	public boolean isEmailErrorRequiredMessageDisplayed () {
		return driver.findElement(By.id("error-email-required")).isDisplayed();
	}

	public boolean isEmailErrorFormatMessageDisaplayed() {
		return driver.findElement(By.id("error-email-format")).isDisplayed();
	}

	public boolean isLibraryErrorRequiredMessageDisplayed() {
		return driver.findElement(By.id("error-library-required")).isDisplayed();
	}

	public void selectPageSize(String count) {
		List<WebElement> options = driver.findElements(By.cssSelector("#count option"));
		for (WebElement el : options) {
			if (el.getAttribute("value").equals(count)) {
				el.click();
				break;
			}
		}
	}
}
