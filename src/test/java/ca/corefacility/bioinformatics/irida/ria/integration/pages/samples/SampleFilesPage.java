package ca.corefacility.bioinformatics.irida.ria.integration.pages.samples;

import java.nio.file.Path;
import java.nio.file.Paths;

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
		return driver.findElement(By.className("t-title")).getText();
	}

	public String getSequenceFileName() {
		return driver.findElements(By.className("file-name")).get(0).getText();
	}

	public int getSequenceFileCount() {
		return driver.findElements(By.className("sequence-file-row")).size();
	}
	
	public int getAssemblyFileCount() {
		return driver.findElements(By.className("assembly_row")).size();
	}
	
	public String getSampleName(){
		return driver.findElement(By.id("sample-name")).getText();
	}
	
	public int getQcEntryCount() {
		return driver.findElements(By.className("qc-item")).size();
	}
	
	public void deleteFirstSequenceFile(){
		WebElement removeButton = driver.findElements(By.className("remove-file")).iterator().next();
		removeButton.click();
		WebElement confirmRemoveButton = (new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(By
				.id("remove-file-confirm")));
		confirmRemoveButton.click();
	}
	
	public void deleteFirstSequenceFilePair(){
		WebElement removeButton = driver.findElements(By.className("remove-pair")).iterator().next();
		removeButton.click();
		WebElement confirmRemoveButton = (new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(By
				.id("remove-file-confirm")));
		confirmRemoveButton.click();
	}
	
	public void deleteFirstAssemblyFile(){
		WebElement removeButton = driver.findElements(By.className("remove-assembly")).iterator().next();
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
