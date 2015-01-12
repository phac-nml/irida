package ca.corefacility.bioinformatics.irida.ria.integration.pages.pipelines;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * <p>Page Object to represent the pipeline selection page.</p>
 *
 * @author Josh Adam<josh.adam@phac-aspc.gc.ca>
 */
public class PipelinesSelectionPage extends AbstractPage {
	private static final String RELATIVE_URL = "pipelines";

	public PipelinesSelectionPage(WebDriver driver) {
		super(driver);
	}

	public void goToPage() {
		get(driver, RELATIVE_URL);
	}

	public boolean arePipelinesDisplayed() {
		return driver.findElements(By.className("card")).size() > 0;
	}
}
