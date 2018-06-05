package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * Represents page found at url: /projects/{projectId}/linelist
 */
public class ProjectLineListPage extends ProjectPageBase {
	private static final String RELATIVE_URL = "/projects/{projectId}/linelist";

	@FindBy(className = "ag-row")
	private List<WebElement> rows;

	public ProjectLineListPage(WebDriver driver) {
		super(driver);
	}

	public static ProjectLineListPage goToPage(WebDriver driver, int projectId) {
		get(driver, RELATIVE_URL.replace("{projectId}", String.valueOf(projectId)));
		return PageFactory.initElements(driver, ProjectLineListPage.class);
	}

	public int getNumberOfRowsInLineList() {
		return rows.size();
	}
}
