package ca.corefacility.bioinformatics.irida.ria.integration.pages.pipelines;

import java.util.List;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class LaunchPipelinePage extends AbstractPage {
	@FindBy(className = "t-pipeline-name")
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

	@FindBy(css = ".t-email-results input")
	private WebElement emailResultsCheckBox;

	@FindBy(className = "t-edit-params-btn")
	private WebElement editParamsButton;

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

	public void updateName(String name) {
		nameInput.sendKeys(Keys.chord(Keys.CONTROL, "a"));
		nameInput.sendKeys(name);
		waitForTime(300);
	}

	public void clearName() {
		nameInput.sendKeys(Keys.chord(Keys.CONTROL, "a"));
		nameInput.sendKeys(Keys.BACK_SPACE);
	}

	public boolean isNameErrorDisplayed() {
		return nameInputError.size() == 1;
	}

	public boolean isEmailResultsChecked() {
		return emailResultsCheckBox.isSelected();
	}

	public void clickEmailResultsCheckbox() {
		emailResultsCheckBox.click();
	}

	public void showEditParameters() {
		editParamsButton.click();
	}

	public void submit() {
		submitBtn.click();
	}
}
