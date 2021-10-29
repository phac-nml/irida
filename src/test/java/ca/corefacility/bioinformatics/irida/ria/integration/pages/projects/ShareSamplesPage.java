package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ShareSamplesPage {
	@FindBy(css = ".t-share-project .ant-select-selection-search-input")
	private WebElement shareProjectSelect;

	@FindBy(className = "ant-select-dropdown")
	private WebElement projectDropdown;

	@FindBy(className = "t-share-button")
	private WebElement shareButton;

	public static ShareSamplesPage initPage(WebDriver driver) {
		return PageFactory.initElements(driver, ShareSamplesPage.class);
	}

	public void searchForProject(String name) {
		shareProjectSelect.sendKeys(name);
		projectDropdown.click();
	}

	public boolean isShareButtonDisabled() {
		return shareButton.isEnabled();
	}

	public void submitShareRequest() {
		shareButton.click();
	}
}
