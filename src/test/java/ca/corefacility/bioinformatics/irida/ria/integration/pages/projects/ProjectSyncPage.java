package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class ProjectSyncPage extends AbstractPage {

	@FindBy(id = "api-selection")
	private WebElement apiSelection;

	@FindBy(id = "project-select")
	private WebElement projectSelection;

	@FindBy(id = "projectUrl")
	private WebElement projectUrlTextBox;

	@FindBy(id = "advancedToggle")
	private WebElement advancedToggle;

	@FindBy(id = "submitBtn")
	private WebElement submitBtn;

	public ProjectSyncPage(WebDriver driver) {
		super(driver);
	}

	public static ProjectSyncPage goTo(WebDriver driver) {
		String url = "/projects/synchronize";
		get(driver, url);
		return PageFactory.initElements(driver, ProjectSyncPage.class);
	}

	public void selectApi(int index) {
		new Select(apiSelection).selectByIndex(index);
	}

	public boolean areProjectsAvailable() {
		return projectSelection.isEnabled();
	}

	public void selectProjectInListing(int index) {
		new Select(projectSelection).selectByIndex(index);
	}

	public void openAdvanced() {
		advancedToggle.click();
	}

	public String getSelectedProjectName() {
		return new Select(projectSelection).getFirstSelectedOption().getText();
	}

	public void setProjectUrl(String url) {
		projectUrlTextBox.clear();
		projectUrlTextBox.sendKeys(url);
	}

	public String getProjectUrl() {
		return projectUrlTextBox.getAttribute("value");
	}

	public void submitProject() {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.elementToBeClickable(submitBtn));
		submitBtn.click();
	}

}
