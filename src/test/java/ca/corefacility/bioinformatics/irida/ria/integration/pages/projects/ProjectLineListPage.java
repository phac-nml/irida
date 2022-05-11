package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Represents page found at url: /projects/{projectId}/linelist
 */
public class ProjectLineListPage extends ProjectPageBase {
	private static final String RELATIVE_URL = "/projects/{projectId}/linelist";

	@FindBy(className = "t-sample-name")
	private List<WebElement> sampleNameLinks;

	@FindBy(className = "t-field-switch")
	private List<WebElement> fieldSwitches;

	@FindBy(className = "ag-header-cell-text")
	private List<WebElement> headerText;

	@FindBy(className = "ant-select-selector")
	private WebElement templateSelectToggle;

	@FindBy(css = ".ant-modal .ant-select-selection-search")
	private WebElement modalTemplateSelectToggle;

	@FindBy(className = "ant-select-selection-search-input")
	private WebElement templateNameInput;

	@FindBy(css = ".ant-modal .ant-select-selection-search-input")
	private WebElement modalTemplateNameInput;

	@FindBy(className = "template-option--name")
	private List<WebElement> templateOptions;

	@FindBy(className = "t-template-save-btn")
	private WebElement templateSaveBtn;

	@FindBy(className = "t-template-name")
	private WebElement templateNameInputWrapper;

	@FindBy(className = "t-modal-save-template-btn")
	private WebElement modalSaveTemplateBtn;

	@FindBy(className = "t-undo-btn")
	private WebElement undoEditBtn;

	@FindBy(className = "t-columns-panel-toggle")
	private WebElement columnsPanelToggle;

	@FindBy(css = ".t-table-filter input")
	private WebElement tableFilterInput;

	@FindBy(className = "t-tour-button")
	private WebElement tourLaunchButton;

	@FindBy(css = "button[data-tour-elem=\"right-arrow\"]")
	private WebElement tourNextButton;

	@FindBy(css = "span[data-tour-elem=\"badge\"")
	private WebElement tourStepBadge;

	@FindBy(className = "t-share-button")
	private WebElement shareButton;

	@FindBy(css = ".ag-pinned-left-cols-container .ag-selection-checkbox")
	private List<WebElement> rowSelectCheckboxes;

	public ProjectLineListPage(WebDriver driver) {
		super(driver);
	}

	public static ProjectLineListPage goToPage(WebDriver driver, int projectId) {
		get(driver, RELATIVE_URL.replace("{projectId}", String.valueOf(projectId)));
		return PageFactory.initElements(driver, ProjectLineListPage.class);
	}

	public void openColumnsPanel() {
		columnsPanelToggle.click();
	}

	public int getNumberOfRowsInLineList() {
		return sampleNameLinks.size();
	}

	public int getNumberOfMetadataFields() {
		return fieldSwitches.size();
	}

	public int getNumberOfTableColumnsVisible() {
		return headerText.size() - 1; // -1 for sample name column
	}

	public void toggleMetadataField(int field) {
		WebElement fieldSwitch = fieldSwitches.get(field);
		waitForElementToBeClickable(fieldSwitch);
		fieldSwitch.click();
		waitForTime(400);
	}

	public void selectTemplate(String template) {
		waitForElementToBeClickable(templateSelectToggle);
		templateSelectToggle.click();
		waitForElementsVisible(By.className("ant-select-dropdown"));
		for (WebElement option : templateOptions) {
			if (option.getText().equals(template)) {
				option.click();
			}
		}
	}

	public void selectModalTemplate(String template) {
		waitForElementToBeClickable(modalTemplateSelectToggle);
		modalTemplateSelectToggle.click();
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.elementToBeClickable(By.className("ant-select-item-option-content")));
		for (WebElement option : templateOptions) {
			if (option.getText().equals(template)) {
				option.click();
			}
		}
	}

	public void saveMetadataTemplate(String name) {
		templateSaveBtn.click();
		waitForElementsVisible(By.className("ant-select-selection-search"));
		templateNameInputWrapper.click();
		modalTemplateNameInput.sendKeys(name);
		modalTemplateNameInput.sendKeys(Keys.ENTER);
		modalSaveTemplateBtn.click();
		waitForElementInvisible(By.className("ant-modal"));
	}

	public String getCellContents(int rowIndex, String columnName) {
		// Need to get the seconds WebElement because the first will be the
		// sample name row.
		WebElement row = driver.findElements(By.cssSelector("*[row-index='" + rowIndex + "']")).get(1);
		WebElement cell = row.findElement(By.cssSelector("*[col-id='" + columnName + "']"));
		return cell.getText();
	}

	public void editCellContents(int rowIndex, String columnName, String newValue) {
		// Need to get the seconds WebElement because the first will be the
		// sample name row.
		WebElement row = driver.findElements(By.cssSelector("*[row-index='" + rowIndex + "']")).get(1);
		WebElement cell = row.findElement(By.cssSelector("*[col-id='" + columnName + "']"));
		cell.click();
		cell.sendKeys(newValue);
		cell.sendKeys(Keys.ENTER);
	}

	public void cancelCellEdit() {
		waitForElementToBeClickable(undoEditBtn);
		undoEditBtn.click();
	}

	public void filterTable(String filter) {
		tableFilterInput.sendKeys(filter);
		waitForTime(500);
	}

	public void clearTableFilter() {
		tableFilterInput.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
		waitForTime(500);
	}

	public void openTour() {
		tourLaunchButton.click();
		waitForElementVisible(By.className("reactour__helper--is-open"));
	}

	public void goToNextTourStage() {
		waitForTime(500);
		tourNextButton.click();
	}

	public void closeTour() {
		driver.findElement(By.cssSelector("body")).sendKeys(Keys.ESCAPE);
		waitForElementInvisible(By.className("reactour__helper--is-open"));
	}

	public int getTourStep() {
		waitForTime(500);
		return Integer.parseInt(tourStepBadge.getText());
	}

	public boolean isImportMetadataBtnVisible() {
		return driver.findElements(By.className("t-import-metadata-btn")).size() > 0;
	}

	public boolean isShareButtonVisible() {
		return driver.findElements(By.className("t-share-btn")).size() > 0;
	}

	public boolean isShareButtonEnabled() {
		return shareButton.isEnabled();
	}

	public void selectRow(int row) {
		rowSelectCheckboxes.get(row).click();
	}

	public void shareSelectedSamples() {
		shareButton.click();
	}
}
