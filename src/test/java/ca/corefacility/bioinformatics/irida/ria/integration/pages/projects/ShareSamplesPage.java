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

	@FindBy(className = "ant-result-success")
	private WebElement successResult;

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

	public boolean isShareButtonDisabled() {
		return shareButton.isEnabled();
	}

	public void submitShareRequest() {
		shareButton.click();
	}

	public boolean isSuccessResultDisplayed() {
        return successResult.isDisplayed();
    }
}
