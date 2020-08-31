package ca.corefacility.bioinformatics.irida.ria.integration.components;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import static ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage.waitForTime;

/**
 * Used to test the state of the React AnalysisQueue component.
 * This component can be found on multiple pages (Analysis listing, Cart, Pipelines).
 */
public class AnalysesQueue {
	@FindBy(className = "t-running-counts")
	private WebElement runningCounts;

	@FindBy(className = "t-queue-counts")
	private WebElement queueCounts;

	public static AnalysesQueue getAnalysesQueue(WebDriver driver) {
		waitForTime(100);
		return PageFactory.initElements(driver, AnalysesQueue.class);
	}

	public int getRunningCounts() {
		waitForTime(100);
		return Integer.parseInt(runningCounts.getText());
	}

	public int getQueueCounts() {
		waitForTime(100);
		return Integer.parseInt(queueCounts.getText());
	}
}