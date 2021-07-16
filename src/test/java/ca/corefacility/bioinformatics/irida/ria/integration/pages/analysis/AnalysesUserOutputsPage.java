package ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class AnalysesUserOutputsPage extends AbstractPage {

	@FindBy(css = "tbody.ant-table-tbody .t-sample-name")
	private List<WebElement> singleSampleAnalysisOutputsRows;

	@FindBy(css = ".ant-input-search .ant-input")
	private WebElement outputsSearchInput;

	@FindBy(className = "ant-input-search-button")
	private WebElement outputsSearchInputSubmit;

	@FindBy(className = "ant-input-clear-icon")
	private WebElement outputsSearchInputClearBtn;

	public AnalysesUserOutputsPage(WebDriver driver) {
		super(driver);
	}

	public static AnalysesUserOutputsPage initializeAnalysesUserSingleSampleAnalysisOutputsPage(WebDriver driver) {
		get(driver, "analysis/user/analysis-outputs");
		return PageFactory.initElements(driver, AnalysesUserOutputsPage.class);
	}

	public int getNumberSingleSampleAnalysisOutputsDisplayed() {
		return singleSampleAnalysisOutputsRows.size();
	}

	public void searchOutputs(String searchStr) {
		waitForElementToBeClickable(outputsSearchInputSubmit);
		outputsSearchInput.sendKeys(searchStr);
		outputsSearchInputSubmit.click();
		waitForElementVisible(By.className("ant-input-clear-icon"));
	}

	public void clearSearchOutputs() {
		waitForElementToBeClickable(outputsSearchInputClearBtn);
		outputsSearchInputClearBtn.click();
		outputsSearchInputSubmit.click();
		waitForElementInvisible(By.className("ant-input-clear-icon"));
	}
}
