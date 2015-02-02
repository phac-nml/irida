package ca.corefacility.bioinformatics.irida.ria.integration.pages.samples;

import org.openqa.selenium.By;
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
	public static final String RELATIVE_URL = "samples/{id}/sequenceFiles";

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
		return driver.findElements(By.className("board-name")).get(0).getText();
	}

	public int getSequenceFileCount() {
		return driver.findElements(By.className("board")).size();
	}
	
	public void deleteFirstFile(){
		WebElement removeButton = driver.findElements(By.className("remove-file")).iterator().next();
		removeButton.click();
		WebElement confirmRemoveButton = (new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(By
				.id("remove-file-confirm")));
		confirmRemoveButton.click();
	}
}
