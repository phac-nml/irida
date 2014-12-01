package ca.corefacility.bioinformatics.irida.ria.integration.pages.samples;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * <p>
 * Page Object to represent the sample sequence files page.
 * </p>
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class SampleFilesPage extends AbstractPage {
	public static final String RELATIVE_URL = "samples/{id}/files";

	public SampleFilesPage(WebDriver driver) {
		super(driver);
	}

	public void gotoPage(Long id) {
		get(driver, RELATIVE_URL.replace("{id}", id.toString()));
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
	
	public void deleteFirstFile(){
		WebElement removeButton = driver.findElements(By.className("remove-file")).iterator().next();
		removeButton.click();
		WebElement confirmRemoveButton = (new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(By
				.className("remove-file-confirm")));
		confirmRemoveButton.click();
	}
	
	public boolean notySuccessDisplayed() {
		boolean present = false;
		try {
			(new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By
					.className("noty_type_success")));
			present = true;
		} catch (NoSuchElementException e) {
			present = false;
		}

		return present;
	}
}
