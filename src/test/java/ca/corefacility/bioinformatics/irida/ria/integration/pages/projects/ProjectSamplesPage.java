package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ca.corefacility.bioinformatics.irida.ria.integration.Select2Utility;

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

	@FindBy(className = "t-sample-tools")
	private WebElement toolsDropdownBtn;

	@FindBy(className = "t-tools-dropdown")
	private WebElement toolsDropdown;

	@FindBy(className = "t-merge")
	private WebElement mergeBtn;

	@FindBy(className = "t-share")
	private WebElement shareBtn;

	@FindBy(className = "t-remove")
	private WebElement removeBtn;

	@FindBy(className = "t-export")
	private WebElement exportSamplesDropdownBtn;

	@FindBy(className = "t-export-dropdown")
	private WebElement exportDropdown;

	@FindBy(className = "t-download")
	private WebElement downloadBtn;

	@FindBy(className = "t-linker")
	private WebElement linkerBtn;

	@FindBy(className = "t-ncbi")
	private WebElement ncbiExportBtn;

	@FindBy(className = "t-create-sample")
	private WebElement createSampleButton;

	@FindBy(className = "t-linker-modal")
	private WebElement linkerModal;

	@FindBy(className = "t-cmd-text")
	private WebElement linkerCmd;

	@FindBy(className = "t-summary")
	private WebElement tableSummary;

	// FILTERS
	@FindBy(css = ".t-td-name .ant-table-filter-trigger")
	private WebElement sampleNameFilterToggle;

	@FindBy(css = ".t-name-select input")
	private WebElement nameFilterInput;

	@FindBy(css = ".t-name-select .ant-select-selector")
	private WebElement nameFilterSelectedOptions;

	@FindBy(css = ".t-td-organism .ant-table-filter-trigger")
	private WebElement organismFilterToggle;

	@FindBy(css = ".t-organism-select input")
	private WebElement organismSelectInput;

	@FindBy(css = ".t-organism-select .ant-select-selector")
	private WebElement organismFilterSelectedOptions;

	@FindBy(css = ".t-td-project .ant-dropdown-trigger")
	private WebElement projectsFilterToggle;

	@FindBy(css = ".t-td-created .ant-table-filter-trigger")
	private WebElement createdDateFilterToggle;

	@FindBy(className = "t-created-filter")
	private WebElement createdDateFilter;

	@FindBy(css = ".t-td-modified .ant-table-filter-trigger")
	private WebElement modifiedDateFilterToggle;

	@FindBy(className = "t-modified-filter")
	private WebElement modifiedDateFilter;

	@FindBy(className = "t-samples-table")
	private WebElement samplesTable;

	//----- OLD BELOW

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

	@FindBy(id = "giveOwner")
	private WebElement giveOwnerBtn;

	@FindBy(className = "t-move-btn")
	private WebElement moveBtn;

	@FindBy(className = "t-add-cart-btn")
	private WebElement addToCartBtn;

	@FindBy(className = "t-remove-modal")
	private WebElement removeModal;

	@FindBy(className = "t-submit-remove")
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

	@FindBy(css = "a.select2-choice")
	private WebElement select2Opener;

	@FindBy(className = "select2-search__field")
	private WebElement select2Input;

	@FindBy(className = "select2-results__options")
	private WebElement select2Results;

	@FindBy(className = "t-filters-btn")
	private WebElement filterByPropertyBtn;

	@FindBy(className = "t-apply-filter-btn")
	private WebElement applyFiltersBtn;

	@FindBy(className = "filter-modal")
	private WebElement filterModal;

	@FindBy(className = "t-clear-filters")
	private WebElement clearFilterBtn;

	// This will be 'Previous', 1, 2, ..., 'Next'
	@FindBy(css = ".pagination li")
	private List<WebElement> pagination;

	// Samples filter date range picker
	@FindBy(className = "t-daterange-filter")
	private WebElement dateRangeInput;

	@FindBy(name = "daterangepicker_start")
	private WebElement daterangepickerStart;

	@FindBy(name = "daterangepicker_end")
	private WebElement daterangepickerEnd;

	@FindBy(css = "div.ranges li")
	private List<WebElement> dateRanges;

	@FindBy(className = "applyBtn")
	private WebElement applyDateRangeBtn;

	@FindBy(id = "selection-main")
	private WebElement selectionMain;

	@FindBy(id = "selection-toggle")
	private WebElement selectionToggle;

	@FindBy(className = "dt-select-all")
	private WebElement selectionAll;

	@FindBy(className = "dt-select-none")
	private WebElement selectionNone;

	@FindBy(id = "linkerCloseBtn")
	private WebElement linkerCloseBtn;

	@FindBy(className = "locked-sample")
	private List<WebElement> lockedSamples;

	@FindBy(css = "[data-dt-idx=\"1\"]")
	private WebElement firstTablePageBtn;

	@FindBy(css = ".paginate_button.next a")
	private WebElement nextTablePageBtn;

	@FindBy(id = "name")
	private WebElement sampleNameInput;

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

	public boolean isSampleToolsAvailable() {
		try {
			return toolsDropdownBtn.isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	public void openToolsDropDown() {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		toolsDropdownBtn.click();
		wait.until(ExpectedConditions.visibilityOf(toolsDropdown));
	}

	public void closeToolsDropdown() {
		closeDropdown(toolsDropdown);
	}

	public boolean isMergeBtnEnabled() {
		return isElementEnabled(mergeBtn);
	}

	public boolean isShareBtnEnabled() {
		return isElementEnabled(shareBtn);
	}

	public boolean isRemoveBtnEnabled() {
		return isElementEnabled(removeBtn);
	}

	public void openExportDropdown() {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		exportSamplesDropdownBtn.click();
		wait.until(ExpectedConditions.visibilityOf(exportDropdown));
	}

	public void closeExportDropdown() {
		closeDropdown(exportDropdown);
	}

	private boolean isElementEnabled(WebElement element) {
		try {
			return !element.getAttribute("aria-disabled").equals("true");
		} catch (Exception e) {
			// If it does not have "aria-disabled" then it is enabled;
			return true;
		}
	}

	private void closeDropdown(WebElement dropdown) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		Actions act = new Actions(driver);
		act.moveByOffset(300, 300).click().perform();
		wait.until(ExpectedConditions.invisibilityOf(dropdown));
	}

	public boolean isDownloadBtnEnabled() {
		return isElementEnabled(downloadBtn);
	}

	public boolean isLinkerBtnEnabled() {
		return isElementEnabled(linkerBtn);
	}

	public TableSummary getTableSummary() {
		return new TableSummary(tableSummary.getText());
	}

	public void openCreateNewSampleModal() {
		openToolsDropDown();
		createSampleButton.click();
	}

	public void filterBySampleName(String name) {
		sampleNameFilterToggle.click();
		nameFilterInput.sendKeys(name);
		nameFilterInput.sendKeys(Keys.ENTER);
		sampleNameFilterToggle.click();
	}

	public void clearIndividualSampleNameFilter(String name) {
		sampleNameFilterToggle.click();
		WebElement filter = nameFilterSelectedOptions.findElement(By.cssSelector("[title=\"" + name + "\"]"));
		filter.findElement(By.className("ant-select-selection-item-remove")).click();
		sampleNameFilterToggle.click();
	}

	public void filterByOrganism(String organism) {
		organismFilterToggle.click();
		organismSelectInput.sendKeys(organism);
		organismSelectInput.sendKeys(Keys.ENTER);
		organismFilterToggle.click();
	}

	public void clearIndividualOrganismFilter(String organism) {
		organismFilterToggle.click();
		WebElement filter = organismFilterSelectedOptions.findElement(By.cssSelector("[title=\"" + organism + "\"]"));
		filter.findElement(By.className("ant-select-selection-item-remove")).click();
		organismFilterToggle.click();
	}

	public void toggleAssociatedProject(String projectName) {
		projectsFilterToggle.click();
		WebElement selection = driver.findElement(
				By.xpath("//li[@class='ant-dropdown-menu-item' and span='" + projectName + "']"));
		selection.click();
		driver.findElement(By.xpath("//button[@type='button' and span='OK']")).click();
		waitForTime(200);
	}

	public void removeAssociatedProject(String projectName) {
		projectsFilterToggle.click();
		WebElement selection = driver.findElement(By.xpath("//li/span/span[text()='" + projectName + "']"));
		selection.click();
		driver.findElement(By.xpath("//button[@type='button' and span='OK']")).click();
		waitForTime(200);
	}

	public void filterByCreatedDate(String start, String end) {
		createdDateFilterToggle.click();
		WebElement startInput = createdDateFilter.findElement(By.xpath("//input[@placeholder='Start date']"));
		startInput.sendKeys(start);
		WebElement endInput = createdDateFilter.findElement(By.xpath("//input[@placeholder='End date']"));
		endInput.sendKeys(end);
		endInput.sendKeys(Keys.ENTER);
		createdDateFilter.findElement(By.className("t-search-btn")).click();
	}

	public void clearFilterByCreatedDate() {
		createdDateFilterToggle.click();
		createdDateFilter.findElement(By.className("t-clear-btn")).click();
		createdDateFilterToggle.click();
	}

	public void filterByModifiedDate(String start, String end) {
		modifiedDateFilterToggle.click();
		WebElement startInput = modifiedDateFilter.findElements(By.xpath("//input[@placeholder='Start date']")).get(1);
		startInput.sendKeys(start);
		WebElement endInput = modifiedDateFilter.findElements(By.xpath("//input[@placeholder='End date']")).get(1);
		endInput.sendKeys(end);
		endInput.sendKeys(Keys.ENTER);
		modifiedDateFilter.findElement(By.className("t-search-btn")).click();
	}

	public void clearFilterByModifiedDate() {
		modifiedDateFilterToggle.click();
		modifiedDateFilter.findElement(By.className("t-clear-btn")).click();
		modifiedDateFilterToggle.click();
	}

	public void selectSampleByName(String sampleName) {
		WebElement checkbox = samplesTable.findElement(By.xpath("//td/a[text()='" + sampleName + "']/../..//input"));
		checkbox.click();
	}

	public void addSelectedSamplesToCart() {
		addToCartBtn.click();
		// Make sure the item were added to the cart.
		waitForElementVisible(By.className("t-cart-count"));
		// If the cart count is already visible this can go too fast,
		// wait for the cart to fully update it's total.
		waitForTime(500);
	}

	// --- OLD BELOW

	public String getTableInfo() {
		return samplesTableInfo.getText();
	}

	public void selectSample(int sampleName) {
		WebElement checkbox = samplesTable.findElement(By.xpath("//td/a[text()= " + sampleName + "]/../..//input"));
		checkbox.click();
	}

	public int getNumberProjectsDisplayed() {
		return tableRows.size();
	}

	public boolean isNcbiBtnEnabled() {
		return isElementEnabled(ncbiExportBtn);
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

	public void selectSampleWithShift(int row) {
		Actions actions = new Actions(driver);
		actions.keyDown(Keys.SHIFT).click(tableRows.get(row).findElement(By.className("t-row-select"))).perform();
		// Sometimes, that shift key never gets lifted!
		actions.keyUp(Keys.SHIFT).perform();
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

	public void waitUntilShareButtonVisible() {
		WebDriverWait wait = openToolsDropdownAndWait();
		wait.until(ExpectedConditions.visibilityOf(shareBtn));
	}

	public void shareSamples() {
		WebDriverWait wait = openToolsDropdownAndWait();
		wait.until(ExpectedConditions.visibilityOf(shareBtn));
		shareBtn.click();
	}

	public void moveSamples(String projectNum) {
		WebDriverWait wait = openToolsDropdownAndWait();
		wait.until(ExpectedConditions.visibilityOf(moveBtn));
		moveBtn.click();
		// Setting owner to false because we removed the checkbox from the move.
		shareMoveSamples(projectNum, false);
	}

	private void openFilterModal() {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		filterByPropertyBtn.click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("modal-content")));
	}

	public void filterByName(String name) {
		openFilterModal();

		nameFilterInput.clear();
		nameFilterInput.sendKeys(name);
		applyFiltersBtn.click();

		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("modal-content")));
	}

	public void filterByDateRange(String range) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		openFilterModal();

		dateRangeInput.clear();
		dateRangeInput.sendKeys(range);

		applyDateRangeBtn.click();
		applyFiltersBtn.click();
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("modal-content")));
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
		Select2Utility select2Utility = new Select2Utility(driver);
		select2Utility.openSelect2Input();
		select2Utility.searchByText(value);
		select2Utility.selectDefaultMatch();
	}

	private void shareMoveSamples(String project, boolean owner) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOf(copySamplesModal));
		enterSelect2Value(project);

		if (owner) {
			try {
				giveOwnerBtn.click();
			} catch (NoSuchElementException e) {
				throw new GiveOwnerNotDisplayedException();
			}
		}

		wait.until(ExpectedConditions.elementToBeClickable(copyModalConfirmBtn));
		copyModalConfirmBtn.click();
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("t-copy-samples-modal")));
	}

	public String getLinkerText() {
		return linkerCmd.getText();
	}

	public void openLinkerModal() {
		openExportDropdown();
		linkerBtn.click();
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOf(linkerModal));
	}

	public void clickLinkerFileType(String type) {
		WebElement fileTypeCheckbox = driver.findElement(By.xpath("//input[@value='" + type + "']"));
		boolean isChecked = fileTypeCheckbox.isSelected();
		fileTypeCheckbox.click();
		WebDriverWait wait = new WebDriverWait(driver, 2);
		wait.until(ExpectedConditions.elementSelectionStateToBe(fileTypeCheckbox, !isChecked));
	}

	public List<String> getLockedSampleNames() {
		List<WebElement> trs = driver.findElements(By.cssSelector("tbody tr"));
		List<String> locked = new ArrayList<>();
		for (WebElement tr : trs) {
			if (tr.findElements(By.className("fa-lock")).size() > 0) {
				locked.add(tr.findElement(By.className("t-sample-label")).getText());
			}
		}
		return locked;
	}

	public void goToNextPage() {
		nextTablePageBtn.click();
		waitForTime(500);
	}

	public void closeModalIfOpen() {
		List<WebElement> modals = driver.findElements(By.className("modal-open"));
		if (modals.size() > 0) {
			Actions actions = new Actions(driver);
			actions.moveToElement(modals.get(0)).moveByOffset(5, 5).click().perform();
		}
	}

	public void enterSampleName(String sampleName) {
		sampleNameInput.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
		sampleNameInput.sendKeys(sampleName);
		waitForTime(1000);
	}

	public boolean isSampleNameErrorDisplayed() {
		return driver.findElements(By.cssSelector(".t-sample-name-wrapper .ant-form-item-explain-error")).size() > 0;
	}

	/**
	 * Exception which is thrown when attempting to give owner to a sample
	 * during copy/move and button is not displayed. Used for verifying no give
	 * owner button when copying remote samples.
	 */
	@SuppressWarnings("serial")
	public static class GiveOwnerNotDisplayedException extends RuntimeException {
	}
}
