package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.components.AntTable;

/**
 * <p>
 * Page Object to represent the projects page.
 * </p>
 *
 */
public class ProjectsPage extends AbstractPage {
	private static AntTable table;

	@FindBy(className = "ant-table-column-title")
	List<WebElement> headers;

	public ProjectsPage(WebDriver driver) {
		super(driver);
	}

	public static ProjectsPage goToProjectsPage(WebDriver driver, boolean isAdmin) {
		table = AntTable.getTable(driver);
		get(driver, "projects" + (isAdmin ? "/all" : ""));
		return PageFactory.initElements(driver, ProjectsPage.class);
	}

	public int getNumberOfProjects() {
		return table.getRows().size();
	}

	public void sortTableByProjectName() {
		table.sortColumn("t-project-name");
	}

	public void searchTableForProjectName(String projectName) {
		table.searchTable(projectName);
	}

	public boolean isTableSortedByProjectNamesAscending() {
		return table.isColumnSortedAscending("t-name");
	}

	public boolean isTableSortedByProjectNamesDescending() {
		return table.isColumnSortDescending("t-name");
	}
}
