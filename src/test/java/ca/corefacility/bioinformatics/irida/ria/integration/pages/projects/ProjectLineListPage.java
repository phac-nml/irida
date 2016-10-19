package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * Page representing the project samples line list.
 */
public class ProjectLineListPage extends AbstractPage {
	@FindBy(css = "#linelist th")
	List<WebElement> tableRows;

	public ProjectLineListPage(WebDriver driver) {
		super(driver);
	}

	public static ProjectLineListPage goToPage(WebDriver driver) {
		get(driver, "/projects/1/linelist");
		return PageFactory.initElements(driver, ProjectLineListPage.class);
	}

	public int getTableHeaderCount() {
		return tableRows.size();
	}
}
