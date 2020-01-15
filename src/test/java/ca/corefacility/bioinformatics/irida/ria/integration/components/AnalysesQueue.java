package ca.corefacility.bioinformatics.irida.ria.integration.components;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class AnalysesQueue {
	@FindBy(className = "t-running-counts")
	private WebElement runningCounts;

	@FindBy(className = "t-queue-counts")
	private WebElement queueCounts;

	public static AnalysesQueue getAnalysesQueue(WebDriver driver) {
		return PageFactory.initElements(driver, AnalysesQueue.class);
	}

	public int getRunningCounts() {
		return Integer.parseInt(runningCounts.getText());
	}

	public int getQueueCounts() {
		return Integer.parseInt(queueCounts.getText());
	}
}
