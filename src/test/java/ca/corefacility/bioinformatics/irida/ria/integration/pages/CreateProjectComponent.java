package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
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
 * <p>
 * Page Object to represent the projects-new page used to create a new project.
 * </p>
 */
public class CreateProjectComponent extends AbstractPage {
	@FindBy(className = "t-create-new-project-btn")
	private WebElement createNewProjectButton;

	@FindBy(className = "t-name-input")
	private WebElement nameInput;

	@FindBy(className = "t-desc-input")
	private WebElement descInput;

	@FindBy(css = ".t-organism-input input")
	private WebElement organismInput;

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

	@FindBy(className = "t-no-samples-selected")
	private WebElement noSamplesSelectedMessage;

	@FindBy(className = "t-no-sample-metadata")
	private WebElement noSampleMetadataMessage;

	@FindBy(css = ".t-samples th .ant-checkbox")
	private WebElement selectAllSamples;

	@FindBy(css = ".ant-form-item-explain div")
	WebElement nameError;

	@FindBy(className = "t-field-label")
	private List<WebElement> metadataFieldLabels;

	@FindBy(className = "t-m-field-label")
	private List<WebElement> projectMetadataFieldLabels;

	@FindBy(className = "t-field-restriction")
	private List<WebElement> projectMetadataFieldRestrictions;

	@FindBy(className = "t-m-field")
	private List<WebElement> projectMetadataFieldRows;

	List<String> expectedLabels = Arrays.asList("City", "Exposures", "PFGE-XbaI-pattern", "Province", "Symptoms");
	List<String> expectedCurrentRestrictions = Arrays.asList("Level 4", "Level 1", "Level 3", "Level 1", "Level 2");
	List<String> expectedTargetRestrictions = Arrays.asList("Level 4", "Level 1", "Level 3", "Level 1", "Level 2");

	public CreateProjectComponent(WebDriver driver) {
		super(driver);
	}

	public static CreateProjectComponent initializeComponent(WebDriver driver) {
		return PageFactory.initElements(driver, CreateProjectComponent.class);
	}

	public void displayForm() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
		createNewProjectButton.click();
		wait.until(ExpectedConditions.visibilityOf(nameInput));
	}

	public void enterProjectName(String name) {
		nameInput.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
		nameInput.sendKeys(name);
		waitForTime(350);
	}

	public void enterProjectDescription(String description) {
		descInput.sendKeys(description);
	}

	public void enterOrganism(String organism) {
		organismInput.sendKeys(organism);
	}

	public void goToNextStep() {
		nextButton.click();
	}

	public void gotToPreviousStep() {
		previousButton.click();
	}

	public void submitProject() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
		finishButton.click();
		wait.until(ExpectedConditions.urlMatches("/projects/[0-9]+"));
	}

	public boolean isNoSamplesMessageDisplayed() {
		return noSamplesMessage.isDisplayed();
	}

	public boolean isNoSamplesSelectedMessageDisplayed() {
		return noSamplesSelectedMessage.isDisplayed();
	}

	public boolean isNoSampleMetadataMessageDisplayed() {
		return noSampleMetadataMessage.isDisplayed();
	}

	public void selectAllSamples() {
		selectAllSamples.click();
	}

	public String getNameWarning() {
		return nameError.getText();
	}

	public boolean correctMetadataFieldDataDisplayed() {
		List<String> fieldLabels = new ArrayList<>();

		for (WebElement element : metadataFieldLabels) {
			fieldLabels.add(element.getText());
		}

		List<String> currentRestrictions = new ArrayList<>();
		List<String> targetRestrictions = new ArrayList<>();

		for (String label : fieldLabels) {
			currentRestrictions.add(driver.findElement(By.className("t-current-restriction-" + label)).getText());
		}

		List<WebElement> targetRestrictionsText = driver.findElements(By.className("ant-radio-button-wrapper-checked"));

		for (WebElement element : targetRestrictionsText) {
			if (!element.getText().isEmpty())
				targetRestrictions.add(element.getText());
		}

		boolean correctLabelsDisplayed = fieldLabels.equals(expectedLabels);
		boolean correctCurrentRestrictionsDisplayed = currentRestrictions.equals(expectedCurrentRestrictions);
		boolean correctTargetRestrictionsDisplayed = targetRestrictions.equals(expectedTargetRestrictions);

		return correctLabelsDisplayed && correctCurrentRestrictionsDisplayed && correctTargetRestrictionsDisplayed;
	}

	public boolean correctMetadataFieldDataDisplayedForNewProject() {
		int mainIndex = 0;
		int textIndex = 0;
		List<WebElement> restrictions = driver.findElements(By.className("ant-radio-button-wrapper-checked"));

		for (WebElement element : projectMetadataFieldRows) {
			String labelText = element.findElement(By.className("t-m-field-label")).getText();
			if (!labelText.equals("Label")) {
				String typeText = element.findElement(By.className("t-m-field-type")).getText();
				String restrictionText = restrictions.get(mainIndex).getText();
				if (!labelText.equals(expectedLabels.get(textIndex)) || !typeText.equals("text")
						|| !restrictionText.equals(expectedCurrentRestrictions.get(textIndex))) {
					return false;
				}
				textIndex++;
			}
			mainIndex++;
		}

		return true;
	}

}
