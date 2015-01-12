package ca.corefacility.bioinformatics.irida.ria.integration.pages.pipelines;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * Phylogenomics workflow launch page.
 *
 * @author Josh Adam<josh.adam@phac-aspc.gc.ca>
 */
public class PipelinesPhylogenomicsPage extends AbstractPage {
	public static final String RELATIVE_URL = "pipelines/phylogenomics";

	public PipelinesPhylogenomicsPage(WebDriver driver) {
		super(driver);
	}

	public void goToPage() {
		get(driver, RELATIVE_URL);
	}

	public int getReferenceFileCount() {
		return driver.findElement(By.id("referenceFiles")).findElements(By.tagName("option")).size();
	}

	public int getNumberofSamplesDisplayed() {
		return driver.findElements(By.className("sample-container")).size();
	}
}
