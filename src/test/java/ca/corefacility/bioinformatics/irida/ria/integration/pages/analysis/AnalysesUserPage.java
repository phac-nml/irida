package ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 */
public class AnalysesUserPage extends AbstractPage {
	@FindBy(css = "tbody.ant-table-tbody tr")
	private List<WebElement> rows;

	@FindBy(className = "t-name")
	private WebElement nameFilterButton;

	@FindBy(className = "t-name-filter")
	private WebElement nameFilterInput;

	@FindBy(className = "t-name-filter-ok")
	private WebElement nameFilterSubmit;

	@FindBy(className = "t-name-filter-clear")
	private WebElement nameFilterClear;


	public AnalysesUserPage(WebDriver driver) {
		super(driver);
	}

	public static AnalysesUserPage initializePage(WebDriver driver) {
		get(driver, "analysis");
		return PageFactory.initElements(driver, AnalysesUserPage.class);
	}

	public int getNumberOfAnalysesDisplayed() {
		return rows.size();}

	public void searchForAnalysisByName(String name) {
		waitForElementToBeClickable(nameFilterButton);
		nameFilterButton.click();
		waitForElementToBeClickable(nameFilterSubmit);
		nameFilterInput.sendKeys(name);
		nameFilterSubmit.click();
	}

	public void clearNameFilter() {
		waitForElementToBeClickable(nameFilterButton);
		nameFilterButton.click();
		waitForElementToBeClickable(nameFilterClear);
		nameFilterClear.click();
	}
}
