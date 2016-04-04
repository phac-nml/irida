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

	@FindBy(id = "samplesTable")
	private WebElement samplesTable;

	@FindBy(id = "selectAllBtn")
	private WebElement selectAll;

	@FindBy(id = "selected-count-info")
	private WebElement selectedCountInfo;

	@FindBy(id = "samplesTable_info")
	private WebElement samplesTableInfo;

	@FindBy(css = "tbody input[type=checkbox]")
	private List<WebElement> sampleCheckboxes;

	@FindBy(id = "mergeBtn")
	private WebElement mergeBtn;

	@FindBy(id = "copyBtn")
	private WebElement copyBtn;

	@FindBy(id = "moveBtn")
	private WebElement moveBtn;

	@FindBy(id = "removeBtn")
	private WebElement removeBtn;

	@FindBy(id = "cart-add-btn")
	private WebElement addToCartBtn;

	@FindBy(id = "remove-samples-modal")
	private WebElement removeModal;

	@FindBy(id = "removeBtnOk")
	private WebElement removeBtnOK;

	@FindBy(className = "merge-modal")
	private WebElement mergeModal;

	@FindBy(id = "confirmMergeBtn")
	private WebElement mergeBtnOK;

	@FindBy(id = "newName")
	private WebElement newMergeNameInput;

	@FindBy(id = "copy-samples-modal")
	private WebElement copySamplesModal;

	@FindBy(id = "projectsSelect")
	private WebElement projectsSelectInput;

	@FindBy(id = "confirm-copy-samples")
	private WebElement copyOkBtn;

	@FindBy(className = "select2-chosen")
	private WebElement select2Opener;

	@FindBy(className = "select2-input")
	private WebElement select2Input;

	@FindBy(className = "select2-results")
	private WebElement select2Results;

	@FindBy(id = "filterByPropertyBtn")
	private WebElement filterByPropertyBtn;

	@FindBy(className = "filter-modal")
	private WebElement filterModal;

	// This will be 'Previous', 1, 2, ..., 'Next'
	@FindBy(css = ".pagination li")
	private List<WebElement> pagination;

	public ProjectSamplesPage(WebDriver driver) {
		super(driver);
	}

	public static ProjectSamplesPage initPage(WebDriver driver) {
		return PageFactory.initElements(driver, ProjectSamplesPage.class);
	}

	public static ProjectSamplesPage gotToPage(WebDriver driver, int projectId) {
		get(driver, RELATIVE_URL + projectId);
		return PageFactory.initElements(driver, ProjectSamplesPage.class);
	}

	public String getTitle() {
		return pageHeader.getText();
	}

	public String getTableInfo() {
		return samplesTableInfo.getText();
	}

	public int getNumberProjectsDisplayed() {
		return sampleCheckboxes.size();
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
		return moveBtn.isEnabled();
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
	}

	public void selectSample(int row) {
		WebElement checkbox = sampleCheckboxes.get(row);
		checkbox.click();
	}

	public void selectSampleWithShift(int row) {
		Actions actions = new Actions(driver);
		actions.keyDown(Keys.SHIFT).click(sampleCheckboxes.get(row)).perform();
	}

	public void selectAllOrNone() {
		selectAll.click();
	}

	public void addSelectedSamplesToCart() {
		addToCartBtn.click();
		// Make sure the item were added to the cart.
		waitForElementVisible(
				By.cssSelector("#cart-count-notification .angular-notifications-icon div"));
	}

	public void removeSamples() {
		removeBtn.click();
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOf(removeModal));
		removeBtnOK.click();
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("remove-modal")));
	}

	public void mergeSamplesWithOriginalName() {
		mergeBtn.click();
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOf(mergeModal));
		mergeBtnOK.click();
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("confirmMergeBtn")));
	}

	public void mergeSamplesWithNewName(String newName) {
		mergeBtn.click();
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOf(mergeModal));
		newMergeNameInput.sendKeys(newName);
		// This wait is for 350 ms because there is a debounce of 300 ms on the input field in which
		// time the AngularJS model on the input does not update - prevents flickering of input error warnings.
		waitForTime(350);
		mergeBtnOK.click();
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("merge-modal")));
	}

	public void copySamples(String project) {
		copyBtn.click();
		copyMoveSamples(project);
	}

	public void moveSamples(String projectNum) {
		moveBtn.click();
		copyMoveSamples(projectNum);
	}

	public void filterByName(String name) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		filterByPropertyBtn.click();
		wait.until(ExpectedConditions.visibilityOf(filterModal));
		WebElement nameInput = filterModal.findElement(By.id("name"));
		nameInput.clear();
		nameInput.sendKeys(name);
		filterModal.findElement(By.id("doFilterBtn")).click();
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("filter-modal")));
	}

	public void clearFilter() {
		clearFilterBtn.click();

	}

	public List<String> getSampleNamesOnPage() {
		List<WebElement> sampleTDs = driver.findElements(By.className("sample-label"));
		List<String> names = new ArrayList<>();
		names.addAll(sampleTDs.stream().map(WebElement::getText).collect(Collectors.toList()));
		return names;
	}

	private void enterSelect2Value(String value) {
		select2Opener.click();
		select2Input.sendKeys(value);
		WebDriverWait wait = new WebDriverWait(driver, 10);
		// Wait needed to allow select2 to populate.
		waitForTime(500);
		select2Input.sendKeys(Keys.RETURN);

		wait.until(ExpectedConditions.not(ExpectedConditions.visibilityOf(select2Results)));
	}

	private void copyMoveSamples(String project) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOf(copySamplesModal));
		enterSelect2Value(project);
		wait.until(ExpectedConditions.elementToBeClickable(copyBtn));
		copyOkBtn.click();
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("copy-modal")));
	}
}
