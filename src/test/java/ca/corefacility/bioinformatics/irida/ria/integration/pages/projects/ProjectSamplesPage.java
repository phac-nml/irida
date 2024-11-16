package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * <p>
 * Page Object to represent the project samples page.
 * </p>
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

	@FindBy(css = ".t-select-all input")
	private WebElement selectAllCheckbox;

	@FindBy(className = "t-merge-modal")
	private WebElement mergeModal;

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

	@FindBy(css = "button.ant-btn.ant-btn-primary")
	private WebElement okButton;

	@FindBy(className = "t-filter-by-file-btn")
	private WebElement filterByFileBtn;

	@FindBy(className = "t-filter-by-file-input")
	private WebElement filterByFileInput;

	@FindBy(className = "t-filter-submit")
	private WebElement filterSubmitBtn;

	@FindBy(className = "t-filter-cancel")
	private WebElement filterCancelBtn;

	@FindBy(className = "ant-pagination-prev")
	private WebElement prevTablePage;

	@FindBy(className = "ant-pagination-next")
	private WebElement nextTablePage;

	public ProjectSamplesPage(WebDriver driver) {
		super(driver);
	}

	public static ProjectSamplesPage initPage(WebDriver driver) {
		return PageFactory.initElements(driver, ProjectSamplesPage.class);
	}

	public static ProjectSamplesPage goToPage(WebDriver driver, Long projectId) {
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
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		toolsDropdownBtn.click();
		wait.until(ExpectedConditions.visibilityOf(toolsDropdown));
	}

	public void closeToolsDropdown() {
		closeDropdown(toolsDropdown);
	}

	public boolean isMergeBtnEnabled() {
		return isElementEnabled(mergeBtn);
	}

	public boolean isMergeBtnVisible() {
		try {
			return mergeBtn.isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isShareBtnEnabled() {
		return isElementEnabled(shareBtn);
	}

	public boolean isRemoveBtnEnabled() {
		return isElementEnabled(removeBtn);
	}

	public void openExportDropdown() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
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
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10L));
		dropdown.sendKeys(Keys.ESCAPE);
		wait.until(ExpectedConditions.invisibilityOf(dropdown));
	}

	public boolean isDownloadBtnEnabled() {
		return isElementEnabled(downloadBtn);
	}

	public boolean isLinkerBtnEnabled() {
		return isElementEnabled(linkerBtn);
	}

	public boolean isNcbiBtnEnabled() {
		return isElementEnabled(ncbiExportBtn);
	}

	public TableSummary getTableSummary() {
		WebElement tableSummary = driver.findElement(By.className("t-summary"));
		return new TableSummary(tableSummary.getText());
	}

	public int getNumberProjectsDisplayed() {
		return tableRows.size();
	}

	public void openCreateNewSampleModal() {
		openToolsDropDown();
		createSampleButton.click();
	}

	public void filterBySampleName(String name) {
		int prevTotal = getTableSummary().getTotal();
		sampleNameFilterToggle.click();
		nameFilterInput.sendKeys(name);
		nameFilterInput.sendKeys(Keys.ENTER);
		waitForTableToUpdate(prevTotal);
	}

	public void clearIndividualSampleNameFilter(String name) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
		int prevTotal = getTableSummary().getTotal();
		sampleNameFilterToggle.click();
		wait.until(ExpectedConditions.visibilityOf(nameFilterSelectedOptions));
		WebElement filter = nameFilterSelectedOptions.findElement(By.cssSelector("[title=\"" + name + "\"]"));
		filter.findElement(By.tagName("svg")).click();
		nameFilterInput.sendKeys(Keys.ESCAPE);
		waitForTableToUpdate(prevTotal);
	}

	public void filterByOrganism(String organism) {
		int prevTotal = getTableSummary().getTotal();
		organismFilterToggle.click();
		organismSelectInput.sendKeys(organism);
		organismSelectInput.sendKeys(Keys.ENTER);
		waitForTableToUpdate(prevTotal);
	}

	public void clearIndividualOrganismFilter(String organism) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
		int prevTotal = getTableSummary().getTotal();
		if(!organismFilterSelectedOptions.isDisplayed()) {
			organismFilterToggle.click();
		}
		wait.until(ExpectedConditions.visibilityOf(organismFilterSelectedOptions));
		WebElement filter = organismFilterSelectedOptions.findElement(By.cssSelector("[title=\"" + organism + "\"]"));
		filter.findElement(By.tagName("svg")).click();
		organismFilterToggle.sendKeys(Keys.ESCAPE);
		waitForTableToUpdate(prevTotal);
	}

	public void toggleAssociatedProject(String projectName) {
		int prevTotal = getTableSummary().getTotal();
		projectsFilterToggle.click();
		WebElement selection = driver.findElement(
				By.xpath("//div[@class='ant-tree-list-holder-inner']//span[@title='" + projectName + "']"));
		selection.click();
		driver.findElement(By.xpath("//button[@type='button' and span='OK']")).click();
		waitForTableToUpdate(prevTotal);
	}

	public void removeAssociatedProject(String projectName) {
		projectsFilterToggle.click();
		WebElement selection = driver.findElement(
				By.xpath("//div[@class='ant-tree-list-holder-inner']//span[@title='" + projectName + "']"));
		selection.click();
		driver.findElement(By.xpath("//button[@type='button' and span='OK']")).click();
		waitForTime(200);
	}

	public void filterByCreatedDate(String start, String end) {
		int prevTotal = getTableSummary().getTotal();
		createdDateFilterToggle.click();
		driver.findElement(By.xpath("//div[@class='t-created-filter']//input[@placeholder='Start date']"))
				.sendKeys(start);
		WebElement endInput = driver.findElement(
				By.xpath("//div[@class='t-created-filter']//input[@placeholder='End date']"));
		endInput.sendKeys(end);
		endInput.sendKeys(Keys.TAB);
		createdDateFilter.findElement(By.className("t-search-btn")).click();
		waitForTableToUpdate(prevTotal);
	}

	public void clearFilterByCreatedDate() {
		int prevTotal = getTableSummary().getTotal();
		createdDateFilterToggle.click();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
		wait.until(ExpectedConditions.visibilityOf(createdDateFilter));
		createdDateFilter.findElement(By.className("t-clear-btn")).click();
		createdDateFilterToggle.click();
		waitForTableToUpdate(prevTotal);
	}

	public void filterByModifiedDate(String start, String end) {
		int prevTotal = getTableSummary().getTotal();
		modifiedDateFilterToggle.click();
		driver.findElement(By.xpath("//div[@class='t-modified-filter']//input[@placeholder='Start date']"))
				.sendKeys(start);
		WebElement endInput = driver.findElement(
				By.xpath("//div[@class='t-modified-filter']//input[@placeholder='End date']"));
		endInput.sendKeys(end);
		endInput.sendKeys(Keys.ENTER);
		modifiedDateFilter.findElement(By.className("t-search-btn")).click();
		waitForTableToUpdate(prevTotal);
	}

	public void clearFilterByModifiedDate() {
		int prevTotal = getTableSummary().getTotal();
		modifiedDateFilterToggle.click();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
		wait.until(ExpectedConditions.visibilityOf(modifiedDateFilter));
		modifiedDateFilter.findElement(By.className("t-clear-btn")).click();
		waitForTableToUpdate(prevTotal);
	}

	public void selectSampleByName(String sampleName) {
		WebElement checkbox = samplesTable.findElement(
				By.xpath("//td/button[span[text()='" + sampleName + "']]/../..//input"));
		checkbox.click();
	}

	public void clickSampleName(String sampleName) {
		WebElement sampleNameLink = samplesTable.findElement(
				By.xpath("//td/button[span[text()='" + sampleName + "']]"));
		sampleNameLink.click();
	}

	public Long getCoverageForSampleByName(String sampleName) {
		WebElement coverageCell = samplesTable.findElement(
				By.xpath("//td/button[span[text()='" + sampleName + "']]/../../td[contains(@class, 't-td-coverage')]"));
		String coverageString = coverageCell.getText();

		return coverageString == null || coverageString.isEmpty() ? null : Long.parseLong(coverageString);
	}

	public void addSelectedSamplesToCart() {
		addToCartBtn.click();
		// Make sure the item were added to the cart.
		waitForElementVisible(By.className("t-cart-count"));
		// If the cart count is already visible this can go too fast,
		// wait for the cart to fully update it's total.
		waitForTime(500);
	}

	public String getLinkerCommand() {
		openLinkerModal();
		String command = linkerCmd.getText();
		closeLinkerModal();
		return command;
	}

	public String getLinkerCommandWithAssembly() {
		openLinkerModal();
		WebElement fileTypeCheckbox = linkerModal.findElement(By.xpath("//input[@value='assembly']"));
		boolean isChecked = fileTypeCheckbox.isSelected();
		fileTypeCheckbox.click();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
		wait.until(ExpectedConditions.elementSelectionStateToBe(fileTypeCheckbox, !isChecked));
		String command = linkerCmd.getText();
		closeLinkerModal();
		return command;

	}

	private void openLinkerModal() {
		openExportDropdown();
		linkerBtn.click();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		wait.until(ExpectedConditions.visibilityOf(linkerModal));
	}

	private void closeLinkerModal() {
		driver.findElement(By.xpath("//button[@type='button' and span='Close']")).click();
	}

	public void toggleSelectAll() {
		boolean checked = selectAllCheckbox.isSelected();
		selectAllCheckbox.click();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
		wait.until(ExpectedConditions.elementSelectionStateToBe(selectAllCheckbox, !checked));
	}

	public void mergeSamplesWithOriginalName(String sampleName) {
		toolsDropdownBtn.click();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		wait.until(ExpectedConditions.visibilityOf(toolsDropdown));
		mergeBtn.click();
		wait.until(ExpectedConditions.visibilityOf(mergeModal));
		WebElement existing = null;
		try {
			mergeModal.findElement(By.xpath("//label[(.//*|.)[contains(text(), '" + sampleName + "')]]")).click();
		} catch (Exception e) {
			driver.findElement(By.className("t-custom-checkbox")).click();
			driver.findElement(By.id("newName")).sendKeys(sampleName);
		}
		driver.findElement(By.xpath("//button[@type='button' and span='Merge Samples']")).click();
		wait.until(ExpectedConditions.textMatches(By.className("t-summary"), Pattern.compile("^Selected: 0")));
	}

	public String getMostRecentlyModifiedSampleName() {
		WebElement nameAnchor = driver.findElement(By.xpath("//tbody/tr[1]/td[2]/button"));
		return nameAnchor.getText();
	}

	public void removeSamples() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
		openToolsDropDown();
		removeBtn.click();
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className("t-remove-modal")));
		removeModal.findElement(By.xpath("//button[@type='button' and span='Remove Samples']")).click();
		// Modal might or might not close depending the outcome of the remove, so we need to wait a moment to
		// see what happens.
		waitForTime(400);
	}

	public boolean isRemoveErrorDisplayed() {
		return driver.findElements(By.className("t-remove-error")).size() > 0;
	}

	public void shareSamples() {
		WebDriverWait wait = openToolsDropdownAndWait();
		wait.until(ExpectedConditions.visibilityOf(shareBtn));
		shareBtn.click();
	}

	private WebDriverWait openToolsDropdownAndWait() {
		toolsDropdownBtn.click();
		return new WebDriverWait(driver, Duration.ofSeconds(10));
	}

	public void enterSampleName(String sampleName) {
		sampleNameInput.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
		sampleNameInput.sendKeys(sampleName);
		waitForTime(1000);
	}

	public void clickOk() {
		okButton.click();
	}

	public boolean isSampleNameErrorDisplayed() {
		return driver.findElements(By.cssSelector(".t-sample-name-wrapper .ant-form-item-explain-error")).size() > 0;
	}

	private void waitForTableToUpdate(int prevTotal) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		wait.until(ExpectedConditions.presenceOfElementLocated(
				By.xpath("//td[contains(@class, 't-summary') and not(text()='Selected: 0 of " + prevTotal + "')]")));
	}

	public void filterByFile(String file1) {
		filterByFileBtn.click();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
		wait.until(ExpectedConditions.visibilityOf(filterByFileInput));
		Path path = Paths.get(file1);
		filterByFileInput.sendKeys(path.toAbsolutePath().toString());
		waitForTime(200);
	}

	public List<String> getInvalidSampleNames() {
		List<String> invalidSampleNames = new ArrayList<>();
		List<WebElement> invalidSampleNamesElements = driver.findElements(By.cssSelector(".t-invalid-sample"));
		for (WebElement invalidSampleNameElement : invalidSampleNamesElements) {
			invalidSampleNames.add(invalidSampleNameElement.getText());
		}
		return invalidSampleNames;
	}

	public void cancelFilterByFile() {
		filterCancelBtn.click();
	}

	public void submitFilterByFile() {
		int total = getTableSummary().getTotal();
		filterSubmitBtn.click();
		waitForTableToUpdate(total);
	}

	public void shareExportSamplesToNcbi() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
		openExportDropdown();
		ncbiExportBtn.click();
		wait.until(ExpectedConditions.urlContains("/ncbi"));
	}

	public void goToNextTablePage() {
		nextTablePage.click();
		waitForTime(200);
	}

	public void gotToPreviousTablePage() {
		prevTablePage.click();
		waitForTime(200);
	}

	public boolean isMessageDisplayed(String message) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(4));
		WebElement notification = wait.until(
				ExpectedConditions.visibilityOfElementLocated(By.className("ant-notification-notice-message")));
		return wait.until(ExpectedConditions.textToBePresentInElement(notification, message));
	}
}
