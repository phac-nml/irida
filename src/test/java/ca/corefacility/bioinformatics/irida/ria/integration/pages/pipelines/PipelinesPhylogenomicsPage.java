package ca.corefacility.bioinformatics.irida.ria.integration.pages.pipelines;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * Phylogenomics workflow launch page.
 *
 * @author Josh Adam<josh.adam@phac-aspc.gc.ca>
 */
public class PipelinesPhylogenomicsPage extends AbstractPage {
	public static final String RELATIVE_URL = "pipelines/phylogenomics";

	public PipelinesPhylogenomicsPage(WebDriver driver) {
		super(driver);
	}

	public void goToPage() {
		get(driver, RELATIVE_URL);
	}

	public int getReferenceFileCount() {
		return driver.findElement(By.id("referenceFiles")).findElements(By.tagName("option")).size();
	}

	public int getNumberofSamplesDisplayed() {
		return driver.findElements(By.className("sample-container")).size();
	}

	public boolean isNoReferenceWarningDisplayed() {
		return  driver.findElements(By.id("no-ref-warning")).size() > 0;
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

	public void clickLaunchPipelineBtn() {
		driver.findElement(By.id("btn-launch")).click();
	}
}
