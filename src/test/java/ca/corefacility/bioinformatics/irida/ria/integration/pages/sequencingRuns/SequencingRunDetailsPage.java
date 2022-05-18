package ca.corefacility.bioinformatics.irida.ria.integration.pages.sequencingRuns;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class SequencingRunDetailsPage extends AbstractPage {

	@FindBy(className = "ant-list-item")
	private List<WebElement> runDetails;
	@FindBy(css = ".t-files-table tbody .ant-table-row")
	private List<WebElement> fileRows;

	public SequencingRunDetailsPage(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}

	public void getDetailsPage(Long id) {
		get(driver, "sequencing-runs/" + id);
	}

	public Map<String, String> getRunDetails() {
		Map<String, String> details = new HashMap<>();
		for (WebElement e : runDetails) {
			String key = e.findElement(By.cssSelector(".ant-list-item-meta-content h4 span strong")).getText();
			String value = e.findElement(By.cssSelector(".ant-list-item-meta-content .ant-list-item-meta-description"))
					.getText();
			details.put(key, value);
		}
		return details;
	}

	public int getTableSize() {
		return fileRows.size();
	}

	public boolean rowExists(String fileName) {
		boolean found = false;
		for (WebElement row : fileRows) {
			if (row.findElement(By.className("t-file-link")).getText().equals(fileName)) {
				found = true;
				break;
			}
		}
		return found;
	}

}
