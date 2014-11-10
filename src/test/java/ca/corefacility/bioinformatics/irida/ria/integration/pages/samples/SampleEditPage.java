package ca.corefacility.bioinformatics.irida.ria.integration.pages.samples;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * <p>
 * Page Object to represent the sample edit page.
 * </p>
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class SampleEditPage extends AbstractPage {
	public static final String RELATIVE_URL = "samples/1/edit";

	public SampleEditPage(WebDriver driver) {
		super(driver);
	}

	public void goToPage() {
		get(driver, RELATIVE_URL);
	}

	// ************************************************************************************************
	// PAGE ELEMENTS
	// ************************************************************************************************

	public boolean isErrorLabelDisplayedForField(String fieldName) {
		return driver.findElements(By.id(fieldName + "-error")).size() > 0;
	}

	// ************************************************************************************************
	// ACTIONS
	// ************************************************************************************************

	public void setFieldValue(String fieldId, String organism) {
		WebElement element = driver.findElement(By.id(fieldId));
		element.clear();
		element.sendKeys(organism);
	}

	public void submitForm() {
		driver.findElement(By.id("submitBtn")).click();
	}
}
