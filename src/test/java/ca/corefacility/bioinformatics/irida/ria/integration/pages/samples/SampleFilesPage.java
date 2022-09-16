package ca.corefacility.bioinformatics.irida.ria.integration.pages.samples;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * <p>
 * Page Object to represent the sample sequence files page.
 * </p>
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

	public String getSequenceFileCreatedDate(String sequenceFileName) {
		WebElement createdDateDiv = driver
				.findElement(By.xpath("//tr[contains(@class, 'sequence-file-row')]/td/a[text()='" + sequenceFileName
						+ "']/../../td/div/div[contains(@class, 'created-date')]"));
		return createdDateDiv.getText();
	}

	public int getSequenceFileCount() {
		return driver.findElements(By.className("sequence-file-row")).size();
	}

	public int getAssemblyFileCount() {
		return driver.findElements(By.className("assembly_row")).size();
	}

	public String getSampleName() {
		return driver.findElement(By.id("sample-name")).getText();
	}

	public int getQcEntryCount() {
		return driver.findElements(By.className("qc-item")).size();
	}

	public void deleteFirstSequenceFile() {
		WebElement removeButton = driver.findElements(By.className("remove-file")).iterator().next();
		removeButton.click();
		WebElement confirmRemoveButton = (new WebDriverWait(driver, 10))
				.until(ExpectedConditions.elementToBeClickable(By.id("remove-file-confirm")));
		confirmRemoveButton.click();
	}

	public void deleteFirstSequenceFilePair() {
		WebElement removeButton = driver.findElements(By.className("remove-pair")).iterator().next();
		removeButton.click();
		WebElement confirmRemoveButton = (new WebDriverWait(driver, 10))
				.until(ExpectedConditions.elementToBeClickable(By.id("remove-file-confirm")));
		confirmRemoveButton.click();
	}

	public void deleteFirstAssemblyFile() {
		WebElement removeButton = driver.findElements(By.className("remove-assembly")).iterator().next();
		removeButton.click();
		WebElement confirmRemoveButton = (new WebDriverWait(driver, 10))
				.until(ExpectedConditions.elementToBeClickable(By.id("remove-file-confirm")));
		confirmRemoveButton.click();
	}

	public boolean isDeleteConfirmationMessageDisplayed() {
		return waitForElementsVisible(By.id("file-deleted-success")).size() > 0;
	}

	public void uploadSequenceFile(String fileName) {
		uploadFile(fileName, 0);
	}

	public void uploadFast5File(String fileName) {
		uploadFile(fileName, 1);
	}

	public void uploadAssemblyFile(String fileName) {
		uploadFile(fileName, 2);
	}

	public boolean isProgressBarDisplayed() {
		return driver.findElements(By.className("progress")).size() > 0;
	}

	private void uploadFile(String filePath, int input) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		WebElement dropdown = driver.findElement(By.className("t-download-dropdown"));
		Actions action = new Actions(driver);
		action.moveToElement(dropdown).perform();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("t-upload-menu")));
		List<WebElement> inputElm = driver.findElements(By.className("t-file-upload-input"));
		Path path = Paths.get(filePath);
		inputElm.get(input).sendKeys(path.toAbsolutePath().toString());
		// Setting wait to ensure file gets uploaded
		// Page blocks refreshes if the file is not completely uploaded
		waitForTime(1000);
	}

	public boolean isFileTypeWarningDisplayed() {
		WebElement notification = waitForElementVisible(By.className("t-file-upload-error"));
		return notification.findElements(By.tagName("li")).size() > 0;
	}
}
