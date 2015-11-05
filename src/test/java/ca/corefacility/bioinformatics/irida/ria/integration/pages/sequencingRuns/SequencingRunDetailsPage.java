package ca.corefacility.bioinformatics.irida.ria.integration.pages.sequencingRuns;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SequencingRunDetailsPage extends AbstractPage {
	public static String PAGEURL = "sequencingRuns/";
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
		driver.findElement(By.id("remove-btn")).click();
		WebElement confirmButton = waitForElementToBeClickable(driver.findElement(By.id("confirm-delete")));
		confirmButton.click();
		waitForElementInvisible(By.className("modal-dialog"));
	}
}
