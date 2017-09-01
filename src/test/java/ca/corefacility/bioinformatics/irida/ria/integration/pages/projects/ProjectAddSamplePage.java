package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;

/**
 * Page to represent the project add new sample page.
 */
public class ProjectAddSamplePage extends AbstractPage {
	public static final String RELATIVE_URL = "/projects/1/samples/new";
	@FindBy(name = "sampleName")
	private WebElement sampleNameInput;

	@FindBy(id = "save-btn")
	private WebElement createBtn;

	@FindBy(id = "sampleName-error") private WebElement sampleNameError;

	@FindBy(css = "a.select2-choice")
	private WebElement organismSelect2;

	@FindBy(css = ".select2-search input")
	private WebElement organismInput;

	public ProjectAddSamplePage(WebDriver driver) {
		super(driver);
	}

	public static ProjectAddSamplePage gotoAsProjectManager(WebDriver driver) {
		LoginPage.loginAsUser(driver);
		get(driver, RELATIVE_URL);
		return PageFactory.initElements(driver, ProjectAddSamplePage.class);
	}

	public static ProjectAddSamplePage gotoAsSystemAdmin(WebDriver driver) {
		LoginPage.loginAsAdmin(driver);
		get(driver, RELATIVE_URL);
		return PageFactory.initElements(driver, ProjectAddSamplePage.class);
	}

	public void enterSampleName(String name) {
		sampleNameInput.clear();
		sampleNameInput.sendKeys(name);
		waitForTime(400);
	}

	public void createSample() {
		createBtn.click();
		waitForElementVisible(By.id("sample-page-title"));
	}

	public boolean isCreateButtonEnabled() {
		return createBtn.isEnabled();
	}

	public boolean isMinLengthNameErrorVisible() {
		return sampleNameError.isDisplayed() && sampleNameError.getText()
				.equals("Sample name must be at least 3 letters.");
	}

	public boolean isRequiredNameErrorVisible() {
		return sampleNameError.isDisplayed() && sampleNameError.getText().equals("Sample name is required.");
	}

	public boolean isInvalidCharactersInNameVisible() {
		return sampleNameError.isDisplayed() && sampleNameError.getText()
				.contains("Sample names should only include letters, numbers, and certain special characters");
	}
}
