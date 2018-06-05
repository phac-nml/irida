package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * Represents page found at url: /projects/{projectId}/linelist
 */
public class ProjectLineListPage extends ProjectPageBase {
	private static final String RELATIVE_URL = "/projects/{projectId}/linelist";

	@FindBy(className = "t-sample-name")
	private List<WebElement> sampleNameLinks;

	@FindBy(className = "t-field-toggle")
	private List<WebElement> fieldToggles;

	@FindBy(className = "ag-header-cell-text")
	private List<WebElement> headerText;

	@FindBy(className = "ant-select-selection-selected-value")
	private WebElement templateSelectToggle;

	@FindBy(className = "template-option--name")
	private List<WebElement> templateOptions;

	@FindBy(className = "t-template-save-btn")
	private WebElement templateSaveBtn;

	@FindBy(className = "ant-select-search__field")
	private WebElement templateNameInput;

	@FindBy(className = "t-modal-save-template-btn")
	private WebElement modalSaveTemplateBtn;

	public ProjectLineListPage(WebDriver driver) {
		super(driver);
	}

	public static ProjectLineListPage goToPage(WebDriver driver, int projectId) {
		get(driver, RELATIVE_URL.replace("{projectId}", String.valueOf(projectId)));
		return PageFactory.initElements(driver, ProjectLineListPage.class);
	}

	public int getNumberOfRowsInLineList() {
		return sampleNameLinks.size();
	}

	public int getNumberOfMetadataFields() {
		return fieldToggles.size();
	}

	public int getNumberOfTableColumnsVisible() {
		return headerText.size();
	}

	public void toggleMetadataField (int field) {
		fieldToggles.get(field).click();
	}

	public void selectTemplate(String template) {
		templateSelectToggle.click();
		waitForElementsVisible(By.className("ant-select-dropdown-menu"));
		for (WebElement option : templateOptions) {
			if (option.getText()
					.equals(template)) {
				option.click();
			}
		}
	}

	public void saveMetadataTemplate (String name) {
		templateSaveBtn.click();
		waitForElementsVisible(By.className("ant-modal"));
		templateNameInput.sendKeys(name);
		modalSaveTemplateBtn.click();
		waitForTime(300);
	}
}
