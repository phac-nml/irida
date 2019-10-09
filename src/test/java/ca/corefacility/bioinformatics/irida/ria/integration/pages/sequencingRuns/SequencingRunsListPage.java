package ca.corefacility.bioinformatics.irida.ria.integration.pages.sequencingRuns;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * Page displaying the list of sequencing runs
 */
public class SequencingRunsListPage extends AbstractPage {
	@FindBy(className = "t-run-link")
	private List<WebElement> runLinks;

	public SequencingRunsListPage(WebDriver driver) {
		super(driver);
	}

	public static SequencingRunsListPage goToPage(WebDriver driver) {
		get(driver, "sequencingRuns/");
		return PageFactory.initElements(driver, SequencingRunsListPage.class);
	}

	public List<Long> getDisplayedIds() {
		return runLinks.stream()
				.map((r) -> Long.parseLong(r.getText()))
				.collect(Collectors.toList());

	}

	public boolean idDisplayIdInList(String id) {
		boolean found = false;
		for (WebElement rowId : runLinks) {
			if (rowId.getText()
					.equals(id)) {
				found = true;
				break;
			}
		}
		return found;
	}
}
