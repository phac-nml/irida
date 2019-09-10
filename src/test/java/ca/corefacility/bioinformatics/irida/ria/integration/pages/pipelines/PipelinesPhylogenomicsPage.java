package ca.corefacility.bioinformatics.irida.ria.integration.pages.pipelines;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * Phylogenomics workflow launch page.
 *
 */
public class PipelinesPhylogenomicsPage extends BasicPipelinePage {

	private static final Logger logger = LoggerFactory.getLogger(PipelinesPhylogenomicsPage.class);
	public PipelinesPhylogenomicsPage(WebDriver driver) {
		super(driver);
	}

	public int getReferenceFileCount() {
		return driver.findElement(By.id("referenceFiles")).findElements(By.tagName("option")).size();
	}

	public String getSelectedParameterSet() {
		return driver.findElement(By.id("named-parameters")).findElement(By.cssSelector("[selected='selected']"))
				.getAttribute("label");
	}

	public boolean isNoReferenceWarningDisplayed() {
		return driver.findElements(By.id("no-ref-warning")).size() > 0;
	}

	public boolean isAddReferenceFileLinksDisplayed() {
		return driver.findElements(By.id("has-rights")).size() > 0;
	}

	public int getAddReferenceFileToProjectLinkCount() {
		return driver.findElements(By.className("add-ref-file")).size();
	}

	public boolean isNameForParametersVisible() {
		waitForElementVisible(By.id("parameterSetName"));
		return true;
	}

	public void clickPipelineParametersBtn() {
		driver.findElement(By.id("pipeline-parameters-btn")).click();
		waitForTime(500);
	}

	public String getParametersModalTitle() {
		return driver.findElement(By.className("modal-title")).getText();
	}

	public String getSNVAbundanceRatio() {
		return driver.findElement(By.id("snv-abundance-ratio")).getAttribute("value");
	}

	public void setSNVAbundanceRatio(String value) {
		final WebElement aaf = driver.findElement(By.id("snv-abundance-ratio"));
		aaf.clear();
		aaf.sendKeys(value);
		waitForTime(500);
	}

	public void setNameForSavedParameters(String value) {
		driver.findElement(By.id("parameterSetName")).clear();
		driver.findElement(By.id("parameterSetName")).sendKeys(value);
	}

	public void clickUseParametersButton() {
		driver.findElement(By.id("para-update-btn")).click();
		waitForTime(500);
	}

	public void clickSaveParameters() {
		driver.findElement(By.id("saveParameters")).click();
		waitForTime(250);
	}

	public void clickSetDefaultSNVAbundanceRatio() {
		driver.findElements(By.xpath("//div[input[@id='snv-abundance-ratio']]/span/button")).get(0).click();
	}

	public void clickSeePipeline() {
		driver.findElement(By.id("btn-see-pipeline")).click();
		waitForTime(250);
	}

	public void clickClearAndFindMore() {
		driver.findElement(By.id("btn-clear-pipeline")).click();
		waitForTime(250);
	}

	public void removeFirstSample() {
		List<WebElement> findElements = driver.findElements(By.className("remove-sample-button"));
		findElements.iterator().next().click();
		waitForTime(250);
	}

	public boolean isCreatePipelineAreaVisible() {
		return driver.findElements(By.id("pipeline-creation")).size() > 0;
	}

	public boolean isRemoteSampleDisplayed(){
		return driver.findElements(By.className("remote-sample-container")).size() > 0;
	}

	public boolean isReferenceFileNameDisplayed(String fileName) {
		return driver.findElement(By.id("uploaded-file-name")).getText().equals(fileName);
	}

	public String selectReferenceFile() throws IOException {
		//create a temp file copy of the test file so it has a unique name
		Path path = Paths.get("src/test/resources/files/test_file.fasta");
		Path tempFile = Files.createTempFile("temp_file", ".fasta");
		Files.copy(path, tempFile, StandardCopyOption.REPLACE_EXISTING);

		WebElement uploadBtn = driver.findElement(By.id("file-upload-button"));
		uploadBtn.sendKeys(tempFile.toAbsolutePath().toString());
		waitForTime(500);

		return tempFile.getFileName().toString();
	}
}
