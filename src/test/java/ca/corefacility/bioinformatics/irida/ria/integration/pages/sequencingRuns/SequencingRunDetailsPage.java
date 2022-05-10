package ca.corefacility.bioinformatics.irida.ria.integration.pages.sequencingRuns;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class SequencingRunDetailsPage extends AbstractPage {
	public static String PAGEURL = "sequencing-runs/";
	private static final Logger logger = LoggerFactory.getLogger(SequencingRunDetailsPage.class);

	public SequencingRunDetailsPage(WebDriver driver) {
		super(driver);
	}

	public void getDetailsPage(Long id) {
		String url = PAGEURL + id;
		logger.debug("Loading sequencing run " + id + " at " + url);
		get(driver, PAGEURL + id);
	}

	public Map<String, String> getRunDetails() {
		logger.debug("Getting run details");
		Map<String, String> details = new HashMap<>();
		List<WebElement> detailElement = driver.findElements(By.className("run-detail"));
		for (WebElement e : detailElement) {
			String key = e.findElement(By.className("run-detail-key")).getText();
			String value = e.findElement(By.className("run-detail-value")).getText();
			logger.debug("Found " + key + " : " + value);
			details.put(key, value);
		}

		return details;
	}

	public String getSequencerType() {
		return driver.findElement(By.className("sequencer-type")).getText();
	}

	public void deleteRun() {
		driver.findElement(By.className("t-remove-btn")).click();
		WebDriverWait wait = new WebDriverWait(driver, 2);
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className("ant-popover-content")));
		WebElement confirmButton = waitForElementToBeClickable(
				driver.findElement(By.cssSelector(".ant-popover-content .ant-btn-primary")));
		confirmButton.click();
	}
}
