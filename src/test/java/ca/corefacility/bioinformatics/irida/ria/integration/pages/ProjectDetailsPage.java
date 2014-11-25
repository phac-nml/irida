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
public class ProjectDetailsPage extends AbstractPage {

	public ProjectDetailsPage(WebDriver driver) {
		super(driver);
	}

	public void goTo(Long projectId) {
		get(driver, "projects/" + projectId);
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
}
