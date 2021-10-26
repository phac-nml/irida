package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * <p>
 * Page Object to represent the projects-new page used to create a new project.
 * </p>
 *
 */
public class CreateProjectComponent extends AbstractPage {
	@FindBy(className = "t-create-new-project-btn")
	private WebElement createNewProjectButton;

	@FindBy(className = "t-name-input")
	private WebElement nameInput;

	@FindBy(className = "t-desc-input")
	private WebElement descInput;

	@FindBy(className = "t-wiki-input")
	private WebElement wikiInput;

	@FindBy(className = "t-create-next-btn")
	private WebElement nextButton;

	@FindBy(className = "t-create-previous-btn")
	private WebElement previousButton;

	@FindBy(className = "t-create-finish-btn")
	private WebElement finishButton;

	@FindBy(className = "t-no-samples")
	private WebElement noSamplesMessage;

	@FindBy(css = ".t-samples th .ant-checkbox")
	private WebElement selectAllSamples;

	@FindBy(css = ".ant-form-item-explain div")
	WebElement nameError;

	public CreateProjectComponent(WebDriver driver) {
		super(driver);
	}

	public static CreateProjectComponent initializeComponent(WebDriver driver) {
		return PageFactory.initElements(driver, CreateProjectComponent.class);
	}

	public void displayForm() {
		WebDriverWait wait = new WebDriverWait(driver, 2);
		createNewProjectButton.click();
		wait.until(ExpectedConditions.visibilityOf(nameInput));
	}

	public void enterProjectName(String name) {
		nameInput.sendKeys(Keys.CONTROL + "a");
		nameInput.sendKeys(Keys.DELETE);
		nameInput.sendKeys(name);
	}

	public void enterProjectDescription(String description) {
		descInput.sendKeys(description);
	}

	public void goToNextStep() {
		nextButton.click();
	}

	public void gotToPreviousStep() {
		previousButton.click();
	}

	public void submitProject() {
		WebDriverWait wait = new WebDriverWait(driver, 2);
		finishButton.click();
		wait.until(ExpectedConditions.urlMatches("/projects/[0-9]+"));
	}

	public boolean isNoSamplesMessageDisplayed() {
		return noSamplesMessage.isDisplayed();
	}

	public void selectAllSamples() {
		selectAllSamples.click();
	}

	public String getNameWarning() {
		WebDriverWait wait = new WebDriverWait(driver, 1);
		wait.until(ExpectedConditions.visibilityOf(nameError));
		return nameError.getText();
	}
}
