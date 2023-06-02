package ca.corefacility.bioinformatics.irida.ria.integration.pages.pipelines;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * This page holds all the form controls that are available on any pipeline launch page.
 */
public class LaunchPipelinePage extends AbstractPage {
	@FindBy(className = "ant-page-header-heading-title")
	private WebElement pipelineName;

	@FindBy(className = "t-submit-btn")
	private WebElement submitBtn;

	@FindBy(className = "t-launch-form")
	private List<WebElement> launchForm;

	@FindBy(className = "t-launch-details")
	private List<WebElement> launchDetails;

	@FindBy(css = ".t-name-control input")
	private WebElement nameInput;

	@FindBy(className = "t-name-required")
	private List<WebElement> nameInputError;

	@FindBy(className = "t-show-parameters")
	private WebElement showSavedParameters;

	@FindBy(className = "t-launch-parameters")
	private List<WebElement> launchParameters;

	@FindBy(className = "t-share-samples")
	private List<WebElement> shareWithSamples;

	@FindBy(className = "t-share-projects")
	private List<WebElement> shareWithProjects;

	@FindBy(className = "t-reference-files")
	private List<WebElement> referenceFiles;

	@FindBy(className = "t-launch-files")
	private List<WebElement> launchFiles;

	@FindBy(css = ".t-email-results .ant-select-selection-item")
	private WebElement emailSelectedValue;

	@FindBy(css = ".t-saved-input")
	private List<WebElement> savedParametersInput;

	@FindBy(className = "t-modified-alert")
	private List<WebElement> modifiedAlert;

	@FindBy(className = "t-modified-name")
	private WebElement modifiedNameInput;

	@FindBy(css = ".t-saved-select .ant-select-selection-item")
	private WebElement savedParametersSelectedValue;

	@FindBy(className = "t-ref-alert")
	private List<WebElement> referencesNotFountAlert;

	@FindBy(className = "t-ref-error")
	private List<WebElement> referencesNotFoundError;

	@FindBy(className = "t-req-param-error")
	private List<WebElement> requiredParamMissingError;

	@FindBy(css = ".t-upload-reference input")
	private WebElement uploadReferenceButton;

	public LaunchPipelinePage(WebDriver driver) {
		super(driver);
	}

	public static LaunchPipelinePage init(WebDriver driver) {
		return PageFactory.initElements(driver, LaunchPipelinePage.class);
	}

	public String getPipelineName() {
		return pipelineName.getText();
	}

	public boolean isLaunchFormDisplayed() {
		return launchForm.size() > 0;
	}

	public boolean isLaunchDetailsDisplayed() {
		return launchDetails.size() == 1;
	}

	public boolean isLaunchParametersDisplayed() {
		return launchParameters.size() == 1;
	}

	public boolean isShareWithSamplesDisplayed() {
		return shareWithSamples.size() == 1;
	}

	public boolean isShareWithProjectsDisplayed() {
		return shareWithProjects.size() == 1;
	}

	public boolean isReferenceFilesDisplayed() {
		return referenceFiles.size() == 1;
	}

	public boolean isLaunchFilesDisplayed() {
		return launchFiles.size() == 1;
	}

	public boolean isReferenceFilesRequiredDisplayed() {
		return referencesNotFountAlert.size() > 0;
	}

	public boolean isReferenceFilesRequiredErrorDisplayed() {
		return referencesNotFoundError.size() > 0;
	}

	public boolean isRequiredParameterErrorDisplayed() {
		return requiredParamMissingError.size() > 0;
	}

	public void updateName(String name) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2L));
		clearName();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.className("t-name-required")));
		nameInput.sendKeys(name);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("t-name-required")));
	}

	/**
	 * Clear the pipeline name input
	 */
	public void clearName() {
		nameInput.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
	}

	public boolean isNameErrorDisplayed() {
		return nameInputError.size() == 1;
	}

	public String getEmailValue() {
		return emailSelectedValue.getText();
	}

	public int getNumberOfSavedPipelineParameters() {
		return savedParametersInput.size();
	}

	public String getSavedParameterValue(String name) {
		Optional<WebElement> input = savedParametersInput.stream()
				.filter(elm -> elm.getAttribute("id").equals("details_" + name))
				.findFirst();
		return input.map(webElement -> webElement.getAttribute("value")).orElse(null);
	}

	public void updateSavedParameterValue(String name, String value) {
		Optional<WebElement> input = savedParametersInput.stream()
				.filter(elm -> elm.getAttribute("id").equals("details_" + name))
				.findFirst();
		if (input.isPresent()) {
			WebElement elm = input.get();
			elm.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
			elm.sendKeys(value);
			elm.sendKeys(Keys.TAB);
		}
	}

	public boolean isModifiedAlertVisible() {
		return modifiedAlert.size() > 0;
	}

	public void saveModifiedTemplateAs(String name) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20L));

		// Scroll to the modified alert
		WebElement modifiedSaveAsButton = driver.findElement(By.className("t-modified-saveas"));
		Actions actions = new Actions(driver);
		actions.moveToElement(modifiedSaveAsButton);
		actions.perform();

		modifiedSaveAsButton.click();
		WebElement saveTemplatePopover = wait.until(
				ExpectedConditions.visibilityOfElementLocated(By.className("t-save-params-form")));
		modifiedNameInput.sendKeys(name);
		saveTemplatePopover.findElement(By.tagName("button")).click();
		wait.until(ExpectedConditions.invisibilityOf(modifiedAlert.get(0)));
	}

	public String getSelectedParametersTemplateName() {
		return savedParametersSelectedValue.getText();
	}

	public void uploadReferenceFile() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
		Path path = Paths.get("src/test/resources/files/test_file.fasta");
		uploadReferenceButton.sendKeys(path.toAbsolutePath().toString());
		wait.until(ExpectedConditions.stalenessOf(referencesNotFountAlert.get(0)));
	}

	public void submit() {
		submitBtn.click();
	}

	public void showSavedParameters() {
		showSavedParameters.click();
	}
}
