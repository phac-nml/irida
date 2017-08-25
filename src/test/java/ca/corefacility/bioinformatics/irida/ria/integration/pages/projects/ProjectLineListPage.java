package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Represents page found at url: /projects/{projectId}/linelist
 */
public class ProjectLineListPage extends ProjectPageBase {
	private static final String RELATIVE_URL = "/projects/{projectId}/linelist";

	@FindBy(css = "#linelist th")
	private List<WebElement> tableHeaders;

	@FindBy(css = "#linelist tbody tr")
	private List<WebElement> tableRows;

	@FindBy(id = "col-vis-btn")
	private WebElement metadataColVisBtn;

	@FindBy(css = ".metadata-open .modal-content")
	private WebElement metadataColVisAside;

	@FindBy(id = "close-aside-btn")
	private WebElement closeAsideBtn;

	@FindBy(className = "bootstrap-switch-label")
	private List<WebElement> colVisBtns;

	@FindBy(id = "template-select")
	private WebElement templateSelect;

	@FindBy(id = "save-btn")
	private WebElement saveBtn;

	@FindBy(id = "template-name")
	private WebElement templateNameInput;


	@FindBy(id = "complete-save")
	private WebElement completeSaveBtn;

	public ProjectLineListPage(WebDriver driver) {
		super(driver);
	}

	public static ProjectLineListPage goToPage(WebDriver driver, int projectId) {
		get(driver, RELATIVE_URL.replace("{projectId}", String.valueOf(projectId)));
		return PageFactory.initElements(driver, ProjectLineListPage.class);
	}

	public int getNumberSamplesWithMetadata() {
		return tableRows.size();
	}

	public int getNumberTableColumns() {
		return tableHeaders.size();
	}

	public void openColumnVisibilityPanel() {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		metadataColVisBtn.click();
		wait.until(ExpectedConditions.visibilityOf(metadataColVisAside));
	}

	public void closeColumnVisibilityPanel() {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		closeAsideBtn.click();
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".modal.in-remove-active")));
	}

	public void toggleColumn(String buttonLabel) {
		for (WebElement btn : colVisBtns) {
			if (btn.getText().equalsIgnoreCase(buttonLabel)) {
				btn.click();
				waitForTime(300);
				break;
			}
		}
	}

	public void selectTemplate(String templateName) {
		Select select = new Select(templateSelect);
		select.selectByVisibleText(templateName);
		waitForTime(2000);
	}

	public void saveTemplate(String templateName) {
		saveBtn.click();
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOf(templateNameInput));
		waitForTime(1000);
		templateNameInput.sendKeys(templateName);
		wait.until(ExpectedConditions.elementToBeClickable(completeSaveBtn));
		completeSaveBtn.click();
		waitForTime(2000);
	}
}
