package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * <p> Project Metadata Page </p>
 *
 */
public class ProjectMetadataPage extends AbstractPage {

	public ProjectMetadataPage(WebDriver driver) {
		super(driver);
	}

	public void goTo(Long projectId) {
		get(driver, "projects/" + projectId + "/metadata");
	}

	public String getDataProjectName() {
		return driver.findElement(By.id("name")).getText();
	}

	public String getDataProjectDescription() {
		return driver.findElement(By.id("description")).getText();
	}

	public String getDataProjectOrganism() {
		return driver.findElement(By.id("organism")).getText();
	}

	public String getDataProjectRemoteURL() {
		return driver.findElement(By.id("remoteURL")).getText();
	}

	public boolean hasEditButton() {
		return driver.findElements(By.id("edit-metadata")).size() > 0;
	}
	
	public Long getProjectId(){
		return Long.parseLong(driver.findElement(By.id("projectIdDisplay")).getText());
	}
}
