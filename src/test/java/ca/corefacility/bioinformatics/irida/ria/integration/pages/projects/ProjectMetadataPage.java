package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * <p>
 * Project Metadata Page
 * </p>
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class ProjectMetadataPage {
    private WebDriver driver;

    public ProjectMetadataPage(WebDriver driver) {
        this.driver = driver;
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

	public int getReferenceFileCount() {
		return driver.findElements(By.cssSelector("#referenceFiles tbody tr")).size();
	}

	public String getReferenceFileName() {
		return driver.findElement(By.cssSelector(".refFileName:nth-of-type(1)")).getText();
	}

    public boolean hasEditButton() {
        return driver.findElements(By.id("edit-metadata")).size() > 0;
    }

	// ************************************************************************************************
	// ACTIONS
	// ************************************************************************************************
	public void clickReferenceFilesTab() {
		driver.findElement(By.cssSelector("ul.nav-tabs li:nth-child(2) a")).click();
	}
}
