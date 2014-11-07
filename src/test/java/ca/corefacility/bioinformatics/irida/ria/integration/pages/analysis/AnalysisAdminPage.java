package ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.BasePage;

/**
 * <p> Page Object to represent the Analysis Admin page. </p>
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class AnalysisAdminPage extends AbstractPage {
	private static final Logger logger = LoggerFactory.getLogger(AnalysisAdminPage.class);
	public static final String RELATIVE_URL = "analysis/admin";

	public AnalysisAdminPage(WebDriver driver) {
		super(driver);
		get(driver, RELATIVE_URL);
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
		BasePage.waitForTime();
		driver.findElement(By.id("filterBtn")).click();
//		pageUtilities.waitForElementVisible(By.id("table-filter"));
	}

	public void filterByName(String name) {

		driver.findElement(By.id("name-filter")).sendKeys(name);
		waitForAjax();
	}

	public void selectStateFilter(String state) {
		WebElement select = driver.findElement(By.name("state-filter"));
		List<WebElement> options = select.findElements(By.tagName("option"));
		for (WebElement option : options) {
			if (option.getText().equals(state)) {
				option.click();
				waitForAjax();
				break;
			}
		}
	}

	public void clickClearFilterButton() {
		driver.findElement(By.id("clearFilterBtn")).click();
		waitForAjax();
	}

	// ************************************************************************************************
	// UTILITY METHODS
	// ************************************************************************************************

	private void waitForAjax() {
		try {
			// There is a 500 ms pause on filtering names.
			Thread.sleep(700);
		} catch (InterruptedException e) {
			logger.error("Cannot sleep the thread.");
		}
	}
}
