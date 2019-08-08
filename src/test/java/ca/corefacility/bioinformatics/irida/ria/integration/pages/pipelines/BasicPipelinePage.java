package ca.corefacility.bioinformatics.irida.ria.integration.pages.pipelines;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * Assembly workflow launch page.
 *
 */
public class BasicPipelinePage extends AbstractPage {

	private static final Logger logger = LoggerFactory.getLogger(BasicPipelinePage.class);

	public BasicPipelinePage(WebDriver driver) {
		super(driver);
	}

	public int getNumberOfSamplesDisplayed() {
		return driver.findElements(By.className("sample-container")).size();
	}

	public boolean isPipelineSubmittedMessageShown() {
		return driver.findElements(By.id("pipeline-submitted")).size() > 0;
	}

	public boolean isPipelineSubmittedSuccessMessageShown() {
		waitForElementVisible(By.id("pipeline-submitted-success"));
		return true;
	}

	public void clickLaunchPipelineBtn() {
		boolean clicked = false;
		do {
			try {
				driver.findElement(By.id("btn-launch")).click();
				clicked = true;
			} catch (final StaleElementReferenceException ex) {
				logger.debug("Got stale element reference exception when clicking launch pipeline, trying again.");
			}
		} while (!clicked);
	}

	public void setNameForAnalysisPipeline(String value) {
		driver.findElement(By.id("pipeline-name")).clear();
		driver.findElement(By.id("pipeline-name")).sendKeys(value);
	}

	public void clickShareResultsWithSamples() {
		driver.findElement(By.id("share-results-samples")).click();
		waitForTime(500);
	}

	public boolean existsShareResultsWithSamples() {
		try {
			driver.findElement(By.id("share-results-samples"));
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	public void clickEmailPipelineResult() {
		driver.findElement(By.id("email-pipeline-result")).click();
		waitForTime(500);
	}

	public boolean existsEmailPipelineResult() {
		try {
			driver.findElement(By.id("email-pipeline-result"));
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}
}
