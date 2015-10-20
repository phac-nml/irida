package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.PageUtilities;

import com.google.common.collect.Ordering;

/**
 * <p>
 * Page Object to represent the project samples page.
 * </p>
 *
 */
public class ProjectSamplesPage extends AbstractPage {
	private static final Logger logger = LoggerFactory.getLogger(ProjectSamplesPage.class);
	private static final String RELATIVE_URL = "projects/1";
	private static final String ALT_RELATIVE_URL = "projects/id";
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

	public int getNumberOfRemoteSamplesDisplayed() {
		return driver.findElements(By.cssSelector(".sample-row.remote-sample")).size();
	}

	public int getGetSelectedPageNumber() {
		return Integer.parseInt(driver.findElement(By.cssSelector(".pagination > .active > a")).getText());
	}

	public void selectPage(int pageNum) {
		List<WebElement> links = driver.findElements(By.cssSelector(".pagination li a"));
		// Remove directions links if there are any
		for (WebElement link : links) {
			if (link.getText().equals("1")) {
				break;
			} else {
				pageNum++;
			}
		}
		// Since paging has an offset of 1
		pageNum--;
		clickAndWait(links.get(pageNum));
	}
	

	/**
	 * Convenience method to make sure that we wait until something has finished
	 * happening after clicking before proceeding to the next step.
	 * 
	 * @param el
	 *            the element to click.
	 */
	public void clickAndWait(final WebElement el) {
		el.click();

		new WebDriverWait(driver, TIME_OUT_IN_SECONDS).until(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver input) {
				return !el.getAttribute("class").contains("active");
			}
		});
	}
	
	private void clickNavigationButton(final String buttonText) {
		final WebElement currentlyActive = driver.findElement(By.cssSelector(".pagination-page.active"));
		final List<WebElement> navigationButtons = driver.findElements(By.cssSelector(".pagination li a"));
		
		for (final WebElement el : navigationButtons) {
			if (el.getText().equals(buttonText)) {
				el.click();
				break;
			}
		}
		
		new WebDriverWait(driver, TIME_OUT_IN_SECONDS).until(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver input) {
				return !currentlyActive.getAttribute("class").contains("active");
			}
		});
	}

	public void clickPreviousPageButton() {
		clickNavigationButton("Previous");
	}
	
	public void clickFirstPageButton() {
		clickNavigationButton("First");
	}

	public void clickNextPageButton() {
		clickNavigationButton("Next");
	}

	public void clickLastPageButton() {
		clickNavigationButton("Last");
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
	
	public void selectSampleByClass(String sampleClass){
		List<WebElement> findElements = driver.findElements(By.cssSelector(".sample-row."+sampleClass));
		WebElement checkbox = findElements.iterator().next().findElement(By.className("large-checkbox"));
		checkbox.click();
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

	public boolean checkWarningNotification() {
		return pageUtilities.checkWarningNotification();
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
		driver.findElement(By.cssSelector("#sortName a")).click();
	}

	public void sortTableByCreatedDate() {
		driver.findElement(By.cssSelector("#sortCreatedDate a")).click();
	}

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
		List<String> names = elms.stream().map(element -> element.getText()).collect(Collectors.toList());
		return Ordering.natural().reverse().isOrdered(names);
	}

	public void enableAssociatedProjects() throws InterruptedException {
		driver.findElement(By.id("displayBtn")).click();
		driver.findElement(By.id("displayAssociated")).click();
		waitForTime(500);
	}

	public void enableRemoteProjects() throws InterruptedException {
		driver.findElement(By.id("displayBtn")).click();
		driver.findElement(By.id("displayRemote")).click();
		waitForTime(500);
	}

	// Filtering
	public int getTotalSampleCount() {
		return Integer.parseInt(driver.findElement(By.id("sample-count")).getText());
	}

	public int getFilteredSampleCount() {
		return Integer.parseInt(driver.findElement(By.id("samples-filtered")).getText());
	}

	public void filterByName(String name) {
		WebElement input = driver.findElement(By.id("sample-name-filter"));
		input.clear();
		new WebDriverWait(driver, TIME_OUT_IN_SECONDS)
				.until(ExpectedConditions.invisibilityOfElementLocated(By.id("samples-filtered")));
		input.sendKeys(name);
		new WebDriverWait(driver, TIME_OUT_IN_SECONDS)
				.until(ExpectedConditions.visibilityOfElementLocated(By.id("samples-filtered")));
	}
	
	public void clearFilterByName() {
		WebElement input = driver.findElement(By.id("sample-name-filter"));
		input.clear();
		new WebDriverWait(driver, TIME_OUT_IN_SECONDS)
				.until(ExpectedConditions.invisibilityOfElementLocated(By.id("samples-filtered")));
	}

	public void filterByOrganism(String organism) {
		WebElement input = driver.findElement(By.id("sample-organism-filter"));
		input.clear();
		new WebDriverWait(driver, TIME_OUT_IN_SECONDS)
				.until(ExpectedConditions.invisibilityOfElementLocated(By.id("samples-filtered")));
		input.sendKeys(organism);
		new WebDriverWait(driver, TIME_OUT_IN_SECONDS)
				.until(ExpectedConditions.visibilityOfElementLocated(By.id("samples-filtered")));
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

	public void filterByFile() {
		WebElement uploadBtn = driver.findElement(By.id("fileFilter"));
		Path path = Paths.get("src/test/resources/files/sampleNamesForFilter.txt");
		uploadBtn.sendKeys(path.toAbsolutePath().toString());
		waitForTime(100);
	}

	// Cart
	public void addSamplesToGlobalCart() {
		driver.findElement(By.id("cart-add-btn")).click();
		waitForTime(500);
	}

	// Sample buttons
	public void showSamplesDropdownMenu() {
		driver.findElement(By.id("samplesOptionsBtn")).click();
	}

	public boolean isSampleMergeOptionEnabled() {
		return !driver.findElement(By.id("merge-li")).getAttribute("class").contains("disabled");
	}

	public boolean isSampleCopyOptionEnabled() {
		return !driver.findElement(By.id("copy-li")).getAttribute("class").contains("disabled");
	}

	public boolean isSampleMoveOptionEnabled() {
		return !driver.findElement(By.id("move-li")).getAttribute("class").contains("disabled");
	}

	public boolean isSampleRemoveOptionEnabled() {
		return !driver.findElement(By.id("remove-li")).getAttribute("class").contains("disabled");
	}
}
