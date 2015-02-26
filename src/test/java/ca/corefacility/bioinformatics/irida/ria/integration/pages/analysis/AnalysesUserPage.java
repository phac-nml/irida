package ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis;

import java.util.List;

import org.openqa.selenium.Keys;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * @author Josh Adam<josh.adam@phac-aspc.gc.ca>
 */
public class AnalysesUserPage extends AbstractPage {
	@FindBy(id = "filter-clear")
	private WebElement filterClear;

	@FindBy(id = "filter-name")
	private WebElement filterName;

	@FindBy(id = "filter-state")
	private WebElement filterState;

	@FindBy(id = "filter-type")
	private WebElement filterType;

	@FindBy(id = "filter-date-early")
	private WebElement filterDateEarly;

	@FindBy(id = "filter-date-late")
	private WebElement filterDateLate;

	@FindBy(className = "analysis__state")
	private List<WebElement> analysesList;

	@FindBy(className = "download-analysis-btn")
	private List<WebElement> downloadAnalysisBtn;

	public AnalysesUserPage(WebDriver driver) {
		super(driver);
	}

	public static AnalysesUserPage initializePage(WebDriver driver) {
		get(driver, "analysis/list");
		return PageFactory.initElements(driver, AnalysesUserPage.class);
	}

	public void clearFilter() {
		filterClear.click();
		waitForTime(100);
	}

	public void filterByName(String name) {
		filterName.clear();
		filterName.sendKeys(name);
		waitForTime(400);
	}

	public void filterByState(String text) {
		Select state = new Select(filterState);
		state.selectByVisibleText(text);
		waitForTime(100);
	}

	public void filterByType(String text) {
		Select type = new Select(filterType);
		type.selectByVisibleText(text);
		waitForTime(100);
	}

	public void filterByDateEarly(String date) {
		filterDateEarly.clear();
		filterDateEarly.sendKeys(date);
		filterDateEarly.sendKeys(Keys.ENTER);
		waitForTime(100);
	}

	public void filterByDateLate(String date) {
		filterDateLate.clear();
		filterDateLate.sendKeys(date);
		filterDateLate.sendKeys(Keys.ENTER);
		waitForTime(100);
	}

	public int getNumberOfAnalyses() {
		return analysesList.size();
	}

	public int getNumberOfDownloadBtns() {
		int count = 0;
		for (WebElement btn : downloadAnalysisBtn) {
			if (btn.isDisplayed()) {
				count++;
			}
		}
		return count;
	}

	public boolean isNoAnalysesMessaageDisplayed() {
		return driver.findElement(By.id("no-analyses")).isDisplayed();
	}
}
