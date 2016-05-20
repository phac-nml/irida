package ca.corefacility.bioinformatics.irida.ria.integration.pages.sequencingRuns;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Page displaying the list of sequencing runs
 * 
 *
 */
public class SequencingRunsListPage extends AbstractPage {

	public static String PAGEURL = "sequencingRuns/";
	private static final Logger logger = LoggerFactory.getLogger(SequencingRunsListPage.class);

	public SequencingRunsListPage(WebDriver driver) {
		super(driver);
	}

	public void goTo() {
		get(driver, PAGEURL);
		// wait for rows to be shown
		waitForElementVisible(By.className("run-id"));
	}

	public List<Long> getDisplayedIds() {
		logger.trace("Listing runs");
		List<WebElement> rows = driver.findElements(By.className("run-id"));
		return rows.stream().map((r) -> {
			return Long.parseLong(r.getText());
		}).collect(Collectors.toList());

	}

	public boolean idDisplayIdInList(String id) {
		logger.trace("Listing runs");
		waitForElementVisible(By.className("run-id"));
		List<WebElement> ids = driver.findElements(By.className("run-id"));
		boolean found = false;
		for (WebElement rowId : ids) {
			if (rowId.getText().equals(id)) {
				found = true;
				break;
			}
		}
		return found;
	}
}
