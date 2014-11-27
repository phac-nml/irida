package ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * <p> Page Object to represent the Analysis Admin page. </p>
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class AnalysisAdminPage extends AbstractPage {
	public static final String RELATIVE_URL = "analysis/admin";

	public AnalysisAdminPage(WebDriver driver) {
		super(driver);
		get(driver, RELATIVE_URL);
		waitForTime(700);
	}

	// ************************************************************************************************
	// FINDERS
	// ************************************************************************************************

	public int getTableRowCount() {
		List<WebElement> elements = driver.findElements(By.className("analysis-row"));
		return elements.size();
	}

	// ************************************************************************************************
	// EVENTS
	// ************************************************************************************************

	public void clickShowFilterButton() {
		WebElement filterBtn = driver.findElement(By.id("filterBtn"));
		waitForElementToBeClickable(filterBtn);
		filterBtn.click();
		waitForElementVisible(By.id("table-filter"));
	}

	public void filterByName(String name) {
		driver.findElement(By.id("name-filter")).sendKeys(name);
		waitForTime(700);
	}

	public void selectStateFilter(String state) {
		WebElement select = driver.findElement(By.name("state-filter"));
		List<WebElement> options = select.findElements(By.tagName("option"));
		for (WebElement option : options) {
			if (option.getText().equals(state)) {
				option.click();
				waitForTime(700);
				break;
			}
		}
	}

	public void clickClearFilterButton() {
		WebElement clearFilterBtn = driver.findElement(By.id("clearFilterBtn"));
		waitForElementToBeClickable(clearFilterBtn);
		clearFilterBtn.click();
		waitForTime(700);
	}
}
