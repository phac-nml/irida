package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * <p>
 * Page Object to represent the projects page.
 * </p>
 */
public class ProjectsPage extends AbstractPage {

	@FindBy(className = "ant-table-row")
	List<WebElement> projectRows;

	@FindBy(css = "thead .ant-table-cell")
	List<WebElement> headers;

	@FindBy(className = "t-name")
	List<WebElement> projectsNameCells;

	@FindBy(css = ".ant-input-search .ant-input")
	WebElement searchInput;

	public ProjectsPage(WebDriver driver) {
		super(driver);
	}

	public static ProjectsPage goToProjectsPage(WebDriver driver, boolean isAdmin) {
		get(driver, "projects" + (isAdmin ? "/all" : ""));
		return PageFactory.initElements(driver, ProjectsPage.class);
	}

	public int getNumberOfProjects() {
		return projectRows.size();
	}

	public void sortProjectTableBy(String columnName) {
		WebElement header;
		for (WebElement webElement : headers) {
			if (webElement.getText()
					.equals(columnName)) {
				header = webElement;
				header.click();
				waitForTime(300);
				break;
			}
		}
	}

	public List<String> getProjectNamesSortList() {
		return projectsNameCells.stream()
				.map(WebElement::getText)
				.collect(Collectors.toList());
	}

	public void clickProjectList(String projectName) {
		projectRows.stream()
				.filter(el -> el.getText()
						.equals(projectName))
				.collect(Collectors.toList())
				.get(0)
				.click();
	}

	public void searchTableForProjectName(String projectName) {
		searchInput.sendKeys(projectName);
		searchInput.sendKeys(Keys.ENTER);
		waitForTime(300);
	}
}
