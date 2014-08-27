package ca.corefacility.bioinformatics.irida.ria.integration.pages.samples;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.BasePage;

/**
 * <p>
 * Page Object to represent the sample sequence files page.
 * </p>
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class SampleFilesPage {
	public static final String URL = BasePage.URL + "samples/{id}/files";
	private WebDriver driver;

	public SampleFilesPage(WebDriver driver) {
		this.driver = driver;
	}

	public void gotoPage(Long id) {
		String url = URL.replace("{id}", id.toString());
		driver.get(url);
	}

	public String getPageTitle() {
		return driver.findElement(By.id("page-title")).getText();
	}

	public String getSequenceFileName() {
		return driver.findElement(By.cssSelector("#filesTable tr:nth-child(1) .file-name")).getText();
	}

	public int getSequenceFileCount() {
		return driver.findElements(By.cssSelector("#filesTable tbody tr")).size();
	}
}
