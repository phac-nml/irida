package ca.corefacility.bioinformatics.irida.ria.integration.pages.samples;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * <p>
 * Page Object to represent the sample sequence files page.
 * </p>
 *
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
		return driver.findElement(By.id("sample-page-title")).getText();
	}

	public String getSequenceFileName() {
		return driver.findElements(By.className("file-name")).get(0).getText();
	}

	public int getSequenceFileCount() {
		return driver.findElements(By.className("sequence-file-row")).size();
	}
	
	public String getSampleName(){
		return driver.findElement(By.id("sample-name")).getText();
	}
	
	public void deleteFirstFile(){
		WebElement removeButton = driver.findElements(By.className("remove-file")).iterator().next();
		removeButton.click();
		WebElement confirmRemoveButton = (new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(By
				.id("remove-file-confirm")));
		confirmRemoveButton.click();
	}
	
	public void deleteFirstPair(){
		WebElement removeButton = driver.findElements(By.className("remove-pair")).iterator().next();
		removeButton.click();
		WebElement confirmRemoveButton = (new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(By
				.id("remove-file-confirm")));
		confirmRemoveButton.click();
	}

	public boolean isDeleteConfirmationMessageDisplayed() {
		return waitForElementsVisible(By.id("file-deleted-success")).size() > 0;
	}

	public void selectGoodFastqFiles() {
		uploadFile("src/test/resources/files/test_file.fastq");
	}

	public void selectBadFastaFile() {
		uploadFile("src/test/resources/files/test_file.fasta");
	}

	public boolean isProgressBarDisplayed() {
		return driver.findElements(By.className("progress")).size() > 0;
	}

	private void uploadFile(String filePath) {
		WebElement uploadBtn = driver.findElement(By.id("file-upload-btn"));
		Path path = Paths.get(filePath);
		uploadBtn.sendKeys(path.toAbsolutePath().toString());
		waitForTime(500);
	}

	public boolean isFileTypeWarningDisplayed() {
		WebElement modalBody = waitForElementVisible(By.className("modal-body"));
		return modalBody.findElement(By.className("bad-file-name")).getText().equals("test_file.fasta");
	}
}
