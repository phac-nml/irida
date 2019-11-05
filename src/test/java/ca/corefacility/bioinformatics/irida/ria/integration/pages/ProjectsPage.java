package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * <p>
 * Page Object to represent the projects page.
 * </p>
 *
 */
public class ProjectsPage extends AbstractPage {
	@FindBy(css = ".ant-table-body table")
	WebElement projectsTable;

	@FindBy(className = "ant-table-column-title")
	List<WebElement> headers;

	@FindBy(css = ".ant-input-search .ant-input")
	WebElement searchInput;

	public ProjectsPage(WebDriver driver) {
		super(driver);
	}

	public static ProjectsPage goToProjectsPage(WebDriver driver, boolean isAdmin) {
		get(driver, "/projects" + (isAdmin ? "/all" : ""));
		return PageFactory.initElements(driver, ProjectsPage.class);
	}

	public int getNumberOfProjects() {
		return projectsTable.findElements(By.cssSelector("tbody tr"))
				.size();
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

	public List<String> getProjectsSortListByColumnName(String columnName) {
		for (int i = 0; i < headers.size(); i++) {
			WebElement header = headers.get(i);
			if (header.getText()
					.equals(columnName)) {
				return projectsTable.findElements(By.cssSelector("tbody tr td:nth-child(" + (i + 1) + ")"))
						.stream()
						.map(WebElement::getText)
						.collect(Collectors.toList());
			}
		}
		return null;
	}

	public void clickProjectList(String projectName) {
		projectsTable.findElements(By.cssSelector("tbody tr td:nth-child(3)"))
				.stream()
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
