package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * <p>
 * Page Object to represent the project samples page.
 * </p>
 *
 */
public class ProjectSamplesPage extends ProjectPageBase {
	private static final String RELATIVE_URL = "projects/";

	@FindBy(tagName = "h1")
	private WebElement pageHeader;

	@FindBy(className = "t-associated-btn")
	private WebElement associatedProjectMenuBtn;

	@FindBy(className = "t-associated-dropdown")
	private WebElement associatedDropdown;

	@FindBy(className = "t-associated-cb")
	private List<WebElement> associatedCbs;

	@FindBy(className = "selected-counts")
	private WebElement selectedCountInfo;

	@FindBy(id = "samplesTable_info")
	private WebElement samplesTableInfo;

	@FindBy(css = "tbody tr")
	private List<WebElement> tableRows;

	@FindBy(className = "t-sample-tools")
	private WebElement toolsDropdownBtn;

	@FindBy(className = "t-merge-btn")
	private WebElement mergeBtn;

	@FindBy(className = "t-copy-btn")
	private WebElement copyBtn;
	
	@FindBy(id = "giveOwner")
	private WebElement giveOwnerBtn;

	@FindBy(className = "t-move-btn")
	private WebElement moveBtn;

	@FindBy(className = "t-remove-btn")
	private WebElement removeBtn;

	@FindBy(className = "t-add-cart-btn")
	private WebElement addToCartBtn;

	@FindBy(id = "remove-samples-modal")
	private WebElement removeModal;

	@FindBy(id = "removeBtnOk")
	private WebElement removeBtnOK;

	@FindBy(id = "merge-samples-modal")
	private WebElement mergeModal;

	@FindBy(id = "confirmMergeBtn")
	private WebElement mergeBtnOK;

	@FindBy(id = "sampleName")
	private WebElement newMergeNameInput;

	@FindBy(className = "t-copy-samples-modal")
	private WebElement copySamplesModal;

	@FindBy(id = "js-confirm")
	private WebElement copyModalConfirmBtn;

	@FindBy(id = "projectsSelect")
	private WebElement projectsSelectInput;

	@FindBy(id = "confirm-copy-samples")
	private WebElement copyOkBtn;

	@FindBy(className = "select2-selection")
	private WebElement select2Opener;

	@FindBy(className = "select2-search__field")
	private WebElement select2Input;

	@FindBy(className = "select2-results__options")
	private WebElement select2Results;

	@FindBy(id = "filterByPropertyBtn")
	private WebElement filterByPropertyBtn;

	@FindBy(className = "filter-modal")
	private WebElement filterModal;

	@FindBy(id = "clearFilterBtn")
	private WebElement clearFilterBtn;

	// This will be 'Previous', 1, 2, ..., 'Next'
	@FindBy(css = ".pagination li")
	private List<WebElement> pagination;

	// Samples filter date range picker
	@FindBy(id = "daterange")
	private WebElement dateRangeInput;

	@FindBy(name = "daterangepicker_start")
	private WebElement daterangepickerStart;

	@FindBy(name = "daterangepicker_end")
	private WebElement daterangepickerEnd;

	@FindBy(css = "div.ranges li")
	private List<WebElement> dateRanges;

	@FindBy(css = ".range_inputs .applyBtn")
	private WebElement applyDateRangeBtn;

	@FindBy(id = "selection-main")
	private WebElement selectionMain;

	@FindBy(id = "selection-toggle")
	private WebElement selectionToggle;

	@FindBy(className = "dt-select-all")
	private WebElement selectionAll;

	@FindBy(className = "dt-select-none")
	private WebElement selectionNone;

	@FindBy(id = "export-samples-btn")
	private WebElement exportSamplesDropdownBtn;

	@FindBy(id = "download-btn")
	private WebElement downloadBtn;

	@FindBy(id = "ncbi-btn")
	private WebElement ncbiBtn;

	@FindBy(css = "#linker-btn a")
	private WebElement linkerBtn;

	@FindBy(className = "linker-modal")
	private WebElement linkerModal;

	@FindBy(id = "linker-cmd")
	private WebElement linkerCmd;

	@FindBy(id = "linkerCloseBtn")
	private WebElement linkerCloseBtn;
	
	@FindBy(className = "locked-sample")
	private List<WebElement> lockedSamples;

	public ProjectSamplesPage(WebDriver driver) {
		super(driver);
	}

	public static ProjectSamplesPage initPage(WebDriver driver) {
		return PageFactory.initElements(driver, ProjectSamplesPage.class);
	}

	public static ProjectSamplesPage gotToPage(WebDriver driver, int projectId) {
		get(driver, RELATIVE_URL + projectId);
		// Wait for full page to get loaded
		waitForTime(800);
		return PageFactory.initElements(driver, ProjectSamplesPage.class);
	}

	public String getTitle() {
		return pageHeader.getText();
	}

	public String getTableInfo() {
		return samplesTableInfo.getText();
	}

	public int getNumberProjectsDisplayed() {
		return tableRows.size();
	}

	public void openToolsDropDown() {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		toolsDropdownBtn.click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("js-mergeBtn")));
	}

	public void openExportDropdown() {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		exportSamplesDropdownBtn.click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("download-btn")));
	}

	public boolean isSampleToolsAvailable() {
		return driver.findElements(By.id("sample-tools")).size() > 0;
	}

	public boolean isDownloadBtnEnabled() {
		return !downloadBtn.getAttribute("class").contains("disabled");
	}

	public boolean isNcbiBtnEnabled() {
		return !ncbiBtn.getAttribute("class").contains("disabled");
	}

	public boolean isMergeBtnEnabled() {
		return mergeBtn.isEnabled();
	}

	public boolean isCopyBtnEnabled() {
		return copyBtn.isEnabled();
	}

	public boolean isMoveBtnEnabled() {
		return moveBtn.isEnabled();
	}

	public boolean isRemoveBtnEnabled() {
		return removeBtn.isEnabled();
	}

	// PAGINATION
	public boolean isPreviousBtnEnabled() {
		return !pagination.get(0).getAttribute("class").contains("disabled");
	}

	public boolean isNextBtnEnabled() {
		return !pagination.get(pagination.size() - 1).getAttribute("class").contains("disabled");
	}

	public int getPaginationCount() {
		// -2 because we ignore the previous and next buttons
		return pagination.size() - 2;
	}

	public String getSelectedInfoText() {
		return selectedCountInfo.getText();
	}

	// Actions
	public void selectPaginationPage(int page) {
		pagination.get(page).findElement(By.cssSelector("a")).click();
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".dataTables_processing")));
	}

	public void selectSample(int row) {
		// Need to get the anything but the first column as that is a link to the sample!
		WebElement checkbox = tableRows.get(row).findElement(By.cssSelector("td input[type='checkbox']"));
		checkbox.click();
	}

	public void selectSampleWithShift(int row) {
		Actions actions = new Actions(driver);
		actions.keyDown(Keys.SHIFT).click(tableRows.get(row).findElement(By.cssSelector("td input[type='checkbox']"))).perform();
		// Sometimes, that shift key never gets lifted!
		actions.keyUp(Keys.SHIFT).perform();
	}

	public void addSelectedSamplesToCart() {
		addToCartBtn.click();
		// Make sure the item were added to the cart.
		waitForElementVisible(
				By.cssSelector("#cart-count"));
		// If the cart count is already visible this can go too fast,
		// wait for the cart to fully update it's total.
		waitForTime(500);
	}

	public void mergeSamplesWithOriginalName() {
		toolsDropdownBtn.click();
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOf(mergeBtn));
		mergeBtn.click();
		wait.until(ExpectedConditions.visibilityOf(mergeModal));
		mergeBtnOK.click();
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("confirmMergeBtn")));
	}

	private WebDriverWait openToolsDropdownAndWait() {
		toolsDropdownBtn.click();
		return new WebDriverWait(driver, 10);
	}

	public void removeSamples() {
		WebDriverWait wait = openToolsDropdownAndWait();
		wait.until(ExpectedConditions.elementToBeClickable(removeBtn));
		removeBtn.click();
		wait.until(ExpectedConditions.visibilityOf(removeModal));
		removeBtnOK.click();
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("remove-modal")));
	}

	public void mergeSamplesWithNewName(String newName) {
		WebDriverWait wait = openToolsDropdownAndWait();
		wait.until(ExpectedConditions.visibilityOf(mergeBtn));
		mergeBtn.click();
		wait.until(ExpectedConditions.visibilityOf(mergeModal));
		newMergeNameInput.sendKeys(newName);
		mergeBtnOK.click();
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("merge-modal")));
	}

	public void copySamples(String project, boolean owner) {
		WebDriverWait wait = openToolsDropdownAndWait();
		wait.until(ExpectedConditions.visibilityOf(copyBtn));
		
		copyBtn.click();
		copyMoveSamples(project, owner);
	}

	public void moveSamples(String projectNum) {
		WebDriverWait wait = openToolsDropdownAndWait();
		wait.until(ExpectedConditions.visibilityOf(moveBtn));
		moveBtn.click();
		copyMoveSamples(projectNum, true);
	}

	public void filterByName(String name) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		filterByPropertyBtn.click();
		wait.until(ExpectedConditions.visibilityOf(filterModal));
		WebElement nameInput = filterModal.findElement(By.id("name"));
		nameInput.clear();
		sendInputTextSlowly(name, nameInput);
		filterModal.findElement(By.id("doFilterBtn")).click();
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("filter-modal")));
		// Ensure that modal fully closed.
		waitForTime(300);
	}

	public void filterByDateRange(String start, String end) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		filterByPropertyBtn.click();
		wait.until(ExpectedConditions.visibilityOf(filterModal));
		dateRangeInput.click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".daterangepicker.show-calendar")));

		Actions builder = new Actions(driver);
		builder.moveToElement(daterangepickerStart, 100, 0).click().build().perform();

		daterangepickerStart.clear();
		sendInputTextSlowly(start, daterangepickerStart);

		builder.moveToElement(daterangepickerEnd, 100, 10).click().build().perform();
		daterangepickerEnd.clear();
		sendInputTextSlowly(end, daterangepickerEnd);
		applyDateRangeBtn.click();

		filterModal.findElement(By.id("doFilterBtn")).click();
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("filter-modal")));
	}

	public void clearFilter() {
		clearFilterBtn.click();
		// Give some time for the filters to properly clear.
		waitForTime(500);
	}

	public List<String> getSampleNamesOnPage() {
		List<WebElement> sampleTDs = driver.findElements(By.cssSelector("tbody td:nth-child(2) a"));
		List<String> names = new ArrayList<>();
		names.addAll(sampleTDs.stream().map(WebElement::getText).collect(Collectors.toList()));
		return names;
	}

	public void displayAssociatedProject() {
		WebDriverWait wait = new WebDriverWait(driver, 10);

		associatedProjectMenuBtn.click();
		wait.until(ExpectedConditions.visibilityOf(associatedDropdown));
		associatedCbs.get(0).click();
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("processingIndicator")));
	}

	public void selectAllSamples() {
		selectionAll.click();
		waitForTime(500);
	}

	public void deselectAllSamples() {
		selectionNone.click();
		waitForTime(500);
	}

	private void enterSelect2Value(String value) {
		select2Opener.click();
		// Wait for select2 to be open properly.
		waitForTime(500);
		sendInputTextSlowly(value, select2Input);
		WebDriverWait wait = new WebDriverWait(driver, 10);
		// Wait needed to allow select2 to populate.
		waitForTime(500);
		select2Input.sendKeys(Keys.RETURN);
	}

	private void copyMoveSamples(String project, boolean owner) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOf(copySamplesModal));
		enterSelect2Value(project);
		
		if(owner){
			giveOwnerBtn.click();
		}
		
		wait.until(ExpectedConditions.elementToBeClickable(copyModalConfirmBtn));
		copyModalConfirmBtn.click();
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("t-copy-samples-modal")));
	}

	public String getLinkerText() {
		openExportDropdown();
		linkerBtn.click();
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOf(linkerModal));
		return linkerCmd.getAttribute("value");
	}
	
	public List<String> getLockedSampleNames(){
		List<WebElement> trs = driver.findElements(By.cssSelector("tbody tr"));
		List<String> locked = new ArrayList<>();
		for (WebElement tr : trs) {
			if (tr.findElements(By.className("fa-lock"))
					.size() > 0) {
				locked.add(tr.findElement(By.className("t-sample-label"))
						.getText());
			}
		}
		return locked;
	}
}
