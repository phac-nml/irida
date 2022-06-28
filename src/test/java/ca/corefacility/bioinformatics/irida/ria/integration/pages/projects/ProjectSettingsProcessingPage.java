package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * Page class for the project settings processing page
 */
public class ProjectSettingsProcessingPage extends AbstractPage {
	@FindBy(className = "t-analysis-template")
	private List<WebElement> existingTemplates;

	@FindBy(className = "t-create-template")
	private List<WebElement> createAnalysisButton;

	@FindBy(className = "t-remove-template")
	private List<WebElement> removeAnalysisButtons;

	@FindBy(className = "t-confirm-remove")
	private WebElement confirmRemoveButton;

	@FindBy(className = "t-template-modal")
	private WebElement templateModal;

	@FindBy(className = "t-select-template")
	private List<WebElement> selectTemplateButtons;

	public ProjectSettingsProcessingPage(WebDriver driver) {
		super(driver);
	}

	public static ProjectSettingsProcessingPage goToPage(WebDriver driver, Long projectId) {
		get(driver, "projects/" + projectId + "/settings/processing");
		WebDriverWait wait = new WebDriverWait(driver, 10L);
		wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//h2[contains(@class, 'ant-typography') and text()='Automated Processing']")));
		return initPage(driver);
	}

	public static ProjectSettingsProcessingPage initPage(WebDriver driver) {
		return PageFactory.initElements(driver, ProjectSettingsProcessingPage.class);
	}

	public boolean isCreateAnalysisButtonVisible() {
		return createAnalysisButton.size() > 0;
	}

	public void clickCreateAnalysis() {
		this.createAnalysisButton.get(0).click();
		WebDriverWait wait = new WebDriverWait(driver, 2);
		wait.until(ExpectedConditions.visibilityOf(templateModal));
	}

	public void removeFirstAnalysis() {
		removeAnalysisButtons.get(0).click();

		WebDriverWait wait = new WebDriverWait(driver, 2);
		wait.until(ExpectedConditions.elementToBeClickable(confirmRemoveButton));
		confirmRemoveButton.click();
	}

	public int countAutomatedAnalyses() {
		return existingTemplates.size();
	}

	public void selectAutomatedTemplateByIndex(int index) {
		selectTemplateButtons.get(index).click();
	}
}
