package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ca.corefacility.bioinformatics.irida.ria.integration.utilities.ProjectEventsUtilities;

/**
 * <p>
 * Page Object to represent the project details page.
 * </p>
 * 
 */
public class ProjectActivityPage extends AbstractPage {

	ProjectEventsUtilities projectEventsSection;

	public ProjectActivityPage(WebDriver driver) {
		super(driver);
		projectEventsSection = new ProjectEventsUtilities(driver);
	}

	public void goTo(Long projectId) {
		get(driver, "projects/" + projectId + "/activity");
	}

	public String getPageTitle() {
		WebElement title = driver.findElement(By.tagName("h1"));
		return title.getText();
	}

	public String getOrganism() {
		WebElement organism = driver.findElement(By.id("project-organism"));
		return organism.getText();
	}

	public String getProjectOwner() {
		WebElement owner = driver.findElement(By.id("project-owner"));
		return owner.getText();
	}

	public String getCreatedDate() {
		WebElement date = driver.findElement(By.id("project-created"));
		return date.getText();
	}

	public String getModifiedDate() {
		WebElement date = driver.findElement(By.id("project-modified"));
		return date.getText();
	}

	public List<WebElement> getEvents() {
		return projectEventsSection.getEvents();
	}
}
