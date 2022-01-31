package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ShareSamplesPage {
	@FindBy(css = ".t-share-project .ant-select-selection-search-input")
	private WebElement shareProjectSelect;

	@FindBy(className = "ant-select-dropdown")
	private WebElement projectDropdown;

	@FindBy(className = "t-share-sample")
	private List<WebElement> shareSampleListItem;

	@FindBy(className = "t-unlocked-sample")
	private List<WebElement> unlockedSamples;

	@FindBy(className = "t-locked-sample")
	private List<WebElement> lockedSamples;

	@FindBy(className = "t-share-button")
	private WebElement shareButton;

	@FindBy(className = "t-share-previous")
	private WebElement previousButton;

	@FindBy(className = "t-share-next")
	private WebElement nextButton;

	@FindBy(className = "ant-result-success")
	private WebElement successResult;

	@FindBy(className = "t-no-sample-warning")
	private WebElement noSamplesWarning;

	@FindBy(className = "t-move-checkbox")
	private WebElement moveCheckbox;

	@FindBy(className = "t-lock-chekcbox")
	private WebElement lockCheckbox;

	@FindBy(className = "t-move-multiple")
	private WebElement moveMultipleSuccess;

	@FindBy(className = "t-share-multiple")
	private WebElement shareMultipleSuccess;

	@FindBy(className = "t-move-single")
	private WebElement moveSingleSuccess;

	@FindBy(className = "t-share-single")
	private WebElement shareSingleSuccess;

	@FindBy(className = "t-same-samples-warning")
	private WebElement someSamplesWarning;

	@FindBy(className = "t-success-title")
	private WebElement successTitle;

	@FindBy(className = "t-field-label")
	private List<WebElement> metadataFieldLabels;

	public static ShareSamplesPage initPage(WebDriver driver) {
		return PageFactory.initElements(driver, ShareSamplesPage.class);
	}

	public void searchForProject(String name) {
		shareProjectSelect.sendKeys(name);
		projectDropdown.click();
	}

	public int getNumberOfSamplesDisplayed() {
		return shareSampleListItem.size();
	}

	public int getNumberOfUnlockedSamples() {
		return unlockedSamples.size();
	}

	public int getNumberOfLockedSamples() {
		return lockedSamples.size();
	}

	public boolean isShareButtonEnabled() {
		return shareButton.isEnabled();
	}

	public boolean isNextButtonEnabled() {
		return nextButton.isEnabled();
	}

	public void gotToNextStep() {
		nextButton.click();
	}

	public boolean isPreviousButtonEnabled() {
		return previousButton.isEnabled();
	}

	public void submitShareRequest() {
		shareButton.click();
	}

	public boolean isSuccessResultDisplayed() {
		return successResult.isDisplayed();
	}

	public boolean isNoSamplesWarningDisplayed() {
		return noSamplesWarning.isDisplayed();
	}

	public void selectMoveCheckbox() {
		moveCheckbox.click();
	}

	public void selectLockCheckbox() {
		lockCheckbox.click();
	}

	public boolean isMoveMultipleSuccessDisplayed() {
		return moveMultipleSuccess.isDisplayed();
	}

	public boolean isShareMultipleSuccessDisplayed() {
		return shareMultipleSuccess.isDisplayed();
	}

	public boolean isMoveSingleSuccessDisplayed() {
		return moveSingleSuccess.isDisplayed();
	}

	public boolean isShareSingleSuccessDisplayed() {
		return shareSingleSuccess.isDisplayed();
	}

	public boolean isSomeSamplesWarningDisplayed() {
		return someSamplesWarning.isDisplayed();
	}

	public String getSuccessTitle() {
		return successTitle.getText();
	}

	public int getNumberOfSharedMetadataEntries() {
		return metadataFieldLabels.size();
	}
}
