package ca.corefacility.bioinformatics.irida.ria.integration.pages.pipelines;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * Phylogenomics workflow launch page.
 *
 */
public class PipelinesPhylogenomicsPage extends AbstractPage {
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

	public int getNumberOfSamplesDisplayed() {
		return driver.findElements(By.className("sample-container")).size();
	}

	public boolean isNoReferenceWarningDisplayed() {
		return driver.findElements(By.id("no-ref-warning")).size() > 0;
	}

	public boolean isNoRightsMessageDisplayed() {
		return driver.findElements(By.id("has-no-rights")).size() > 0;
	}

	public boolean isAddReferenceFileLinksDisplayed() {
		return driver.findElements(By.id("has-rights")).size() > 0;
	}

	public int getAddReferenceFileToProjectLinkCount() {
		return driver.findElements(By.className("add-ref-file")).size();
	}

	public boolean isPipelineSubmittedMessageShown() {
		return driver.findElements(By.id("pipeline-submitted")).size() > 0;
	}

	public boolean isPipelineSubmittedSuccessMessageShown() {
		waitForElementVisible(By.id("pipeline-submitted-success"));
		return true;
	}
	
	public boolean isNameForParametersVisible() {
		waitForElementVisible(By.id("parameterSetName"));
		return true;
	}

	public void clickLaunchPipelineBtn() {
		driver.findElement(By.id("btn-launch")).click();
	}

	public void clickPipelineParametersBtn() {
		driver.findElement(By.id("pipeline-parameters-btn")).click();
		waitForTime(200);
	}

	public String getParametersModalTitle() {
		return driver.findElement(By.className("modal-title")).getText();
	}

	public String getAlternativeAlleleFractionValue() {
		return driver.findElements(By.className("para-input")).get(0).getText();
	}

	public void setAlternativeAlleleFraction(String value) {
		driver.findElements(By.className("para-input")).get(0).sendKeys(value);
	}
	
	public void setNameForSavedParameters(String value) {
		driver.findElement(By.id("parameterSetName")).clear();
		driver.findElement(By.id("parameterSetName")).sendKeys(value);
	}
	
	public void clickUseParametersButton() {
		driver.findElement(By.id("para-update-btn")).click();
		waitForTime(250);
	}
	
	public void clickSaveParameters() {
		driver.findElement(By.id("saveParameters")).click();
		waitForTime(250);
	}

	public void clickSetDefaultAlternativeAlleleFraction() {
		driver.findElements(By.className("set-default-btn")).get(0).click();
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
}
