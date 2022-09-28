package ca.corefacility.bioinformatics.irida.ria.integration.pages.sequencingRuns;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * Page displaying the list of sequencing runs
 */
public class SequencingRunListPage extends AbstractPage {
	@FindBy(css = ".t-runs-table tbody .ant-table-row")
	private List<WebElement> runRows;

	public SequencingRunListPage(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}

	public void goToPage() {
		get(driver, "sequencing-runs/");
	}

	public int getTableSize() {
		return runRows.size();
	}

	public boolean rowExists(String runId) {
		boolean found = false;
		for (WebElement row : runRows) {
			if (row.findElement(By.className("t-run-details-link")).getText().equals(runId)) {
				found = true;
				break;
			}
		}
		return found;
	}

	public void deleteRun(String runId) {
		for (WebElement row : runRows) {
			if (row.findElement(By.className("t-run-details-link")).getText().equals(runId)) {
				row.findElement(By.className("t-run-remove-link")).click();

				WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
				wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.className("ant-popover-content"))));
				WebElement confirmButton = waitForElementToBeClickable(
						driver.findElement(By.cssSelector(".ant-popover-content .ant-btn-primary")));
				confirmButton.click();
				wait.until(ExpectedConditions.invisibilityOf(driver.findElement(By.className("ant-popover-content"))));
				break;
			}
		}
	}

}
