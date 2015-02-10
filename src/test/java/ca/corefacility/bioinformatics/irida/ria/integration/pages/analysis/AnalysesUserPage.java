package ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis;

import java.util.List;

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

	@FindBy(className = "board")
	private List<WebElement> analysesList;

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

	public void filterByType(String text) {
		Select state = new Select(filterState);
		state.selectByVisibleText(text);
		waitForTime(100);
	}

	public int getNumberOfAnalyses() {
		return analysesList.size();
	}
}
