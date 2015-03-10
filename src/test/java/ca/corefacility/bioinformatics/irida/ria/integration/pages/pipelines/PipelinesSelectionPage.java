package ca.corefacility.bioinformatics.irida.ria.integration.pages.pipelines;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * <p>Page Object to represent the pipeline selection page.</p>
 *
 */
public class PipelinesSelectionPage extends AbstractPage {
	private static final String RELATIVE_URL = "pipelines";

	@FindBy(id = "phylogenomics_btn")
	private static WebElement phylogenomicsBtn;

	public PipelinesSelectionPage(WebDriver driver) {
		super(driver);
	}

	public void goToPage() {
		get(driver, RELATIVE_URL);
	}

	public boolean arePipelinesDisplayed() {
		return driver.findElements(By.className("card")).size() > 0;
	}

	public static void goToPhylogenomicsPipeline(WebDriver webDriver) {
		get(webDriver, RELATIVE_URL);
		PipelinesSelectionPage page = PageFactory.initElements(webDriver, PipelinesSelectionPage.class);
		page.clickElement(phylogenomicsBtn);
	}
}
