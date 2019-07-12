package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

/**
 * Page class for the project settings processing page
 */
public class ProjectSettingsProcessingPage extends AbstractPage {

	private static final String CREATE_BUTTON_ID = "create-auto-analysis";
	@FindBy(id = CREATE_BUTTON_ID)
	private WebElement createAnalysisButton;

	@FindBy(className = "auto-analysis-status")
	private List<WebElement> automatedAnalyses;

	@FindBy(className = "t-remove-auto-analysis")
	private List<WebElement> removeAnalysisButtons;

	public ProjectSettingsProcessingPage(WebDriver driver) {
		super(driver);
	}

	public static ProjectSettingsProcessingPage goToPage(WebDriver driver, Long projectId) {
		waitForTime(800);
		get(driver, "projects/" + projectId + "/settings");
		return initPage(driver);
	}

	public static ProjectSettingsProcessingPage initPage(WebDriver driver) {
		return PageFactory.initElements(driver, ProjectSettingsProcessingPage.class);
	}

	public boolean isCreateAnalysisButtonVisible() {
		return driver.findElements(By.id(CREATE_BUTTON_ID))
				.size() > 0;
	}

	public void clickCreateAnalysis() {
		this.createAnalysisButton.click();
	}

	public void removeFirstAnalysis() {
		removeAnalysisButtons.iterator()
				.next()
				.click();

		waitForElementsVisible(By.id("remove-template-button"));

		driver.findElement(By.id("remove-template-button"))
				.click();
	}

	public int countAutomatedAnalyses() {
		return automatedAnalyses.size();
	}
}
