package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * <p>
 * Page Object to represent the projects page.
 * </p>
 */
public class ProjectsPage extends AbstractPage {
	@FindBy(css = ".ant-table-content table")
	WebElement projectsTable;

	@FindBy(className = "ant-table-column-title")
	List<WebElement> headers;

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
		return projectsTable.findElements(By.className("ant-table-row"))
				.size();
	}

	public void sortProjectTableBy() {
		projectsTable.findElement(By.className("t-name-col")).click();
		waitForTime(200);
	}

	public List<String> getProjectsSortListByColumnName() {
		return projectsTable.findElements(By.className("t-name"))
				.stream()
				.map(WebElement::getText)
				.collect(Collectors.toList());
	}

	public void searchTableForProjectName(String projectName) {
		searchInput.sendKeys(projectName);
		searchInput.sendKeys(Keys.ENTER);
		waitForTime(300);
	}
}
