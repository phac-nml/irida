package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * Result page for global search
 */
public class SearchResultPage extends AbstractPage {

	@FindBy(id = "sample-count")
	private WebElement sampleCount;

	@FindBy(id = "project-count")
	private WebElement projectCount;

	public SearchResultPage(WebDriver driver) {
		super(driver);
	}

	public static SearchResultPage initPage(WebDriver driver) {
		return PageFactory.initElements(driver, SearchResultPage.class);
	}

	public void waitForSearchResults() {
		int count = 0;
		boolean found = false;
		do {
			waitForTime(100);

			List<WebElement> spinners = driver.findElements(By.className("search-spinner"));

			found = spinners.isEmpty();

			if (count > 5) {
				throw new RuntimeException("Search did not complete");
			}

			count++;

		} while (!found);

	}

	public int getSampleCount() {
		return Integer.parseInt(sampleCount.getText());
	}

	public int getProjectCount() {
		return Integer.parseInt(projectCount.getText());
	}
}
