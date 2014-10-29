package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.BasePage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.PageUtilities;

/**
 * <p>
 * Page Object to represent the project samples page.
 * </p>
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class ProjectSamplesPage {
	public static final String DATE_FORMAT = "dd MMM YYYY";
	private static final String URL = BasePage.URL + "/projects/1/samples";
	private PageUtilities pageUtilities;
	private WebDriver driver;

	public ProjectSamplesPage(WebDriver driver) {
		this.driver = driver;
		this.pageUtilities = new PageUtilities(driver);
	}

	public void goTo() {
		driver.get(URL);
		pageUtilities.waitForElementVisible(By.className("sample-row"));
	}

	/**
	 * The the h1 heading for the page
	 *
	 * @return String value from within the h1 tag
	 */
	public String getTitle() {
		return driver.findElement(By.tagName("h1")).getText();
	}

	public int getNumberOfSamplesDisplayed() {
		return driver.findElements(By.className("sample-row")).size();
	}

	public int getNumberOfSamplesSelected() {
		return driver.findElements(By.cssSelector(".sample-select:checked")).size();
	}

	public void selectSampleByRow(int row) {
		List<WebElement> inputs = driver.findElements(By.className("sample-select"));
		inputs.get(row).click();
	}
}
