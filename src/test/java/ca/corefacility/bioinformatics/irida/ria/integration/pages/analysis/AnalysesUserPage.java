package ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 */
public class AnalysesUserPage extends AbstractPage {
	@FindBy(css = "tbody.ant-table-tbody .t-name")
	private List<WebElement> rows;

	@FindBy(css = ".ant-table-selection-column .ant-checkbox-wrapper")
	private List<WebElement> rowCheckboxes;

	@FindBy(className = "t-delete-selected")
	private WebElement deleteSelectedBtn;

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

	public static AnalysesUserPage initializeAdminPage(WebDriver driver) {
		get(driver, "analysis/all");
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

	public void deleteAnalysis(int row) {
		waitForElementToBeClickable(rowCheckboxes.get(row)).click();
		waitForElementToBeClickable(deleteSelectedBtn).click();
		WebElement popover = waitForElementVisible(By.className("ant-popover-inner-content"));
		popover.findElement(By.cssSelector(".ant-btn.ant-btn-primary.ant-btn-sm")).click();
		waitForTime(500);
	}
}
