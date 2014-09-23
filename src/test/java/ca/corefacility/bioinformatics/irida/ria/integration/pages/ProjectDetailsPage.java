package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * <p>
 * Page Object to represent the project details page.
 * </p>
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class ProjectDetailsPage {
    private WebDriver driver;

	public ProjectDetailsPage(WebDriver driver, Long projectId) {
		this.driver = driver;
		driver.get("http://localhost:8080/projects/" + projectId);
	}

	public String getPageTitle() {
		WebElement title = driver.findElement(By.id("wb-cont"));
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
}
