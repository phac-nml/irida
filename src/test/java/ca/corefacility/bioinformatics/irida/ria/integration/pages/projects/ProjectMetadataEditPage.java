package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Project Edit Metadata Page
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class ProjectMetadataEditPage {
    public static final String ATTR_PLACEHOLDER = "placeholder";
    private WebDriver driver;

    public ProjectMetadataEditPage(WebDriver driver) {
        this.driver = driver;
    }

    public String getNamePlaceholder() {
        return driver.findElement(By.name("name")).getAttribute(ATTR_PLACEHOLDER);
    }

    public String getOrganismPlaceholder() {
        return driver.findElement(By.name("organism")).getAttribute(ATTR_PLACEHOLDER);
    }

    public String getDescriptionPlaceholder() {
        return driver.findElement(By.name("projectDescription")).getAttribute(ATTR_PLACEHOLDER);
    }

    public String getRemoteURLPlaceholder() {
        return driver.findElement(By.name("remoteURL")).getAttribute(ATTR_PLACEHOLDER);
    }

    public void updateProject(String name, String organism, String projectDescription, String remoteURL) {
        driver.findElement(By.name("name")).sendKeys(name);
        driver.findElement(By.name("organism")).sendKeys(organism);
        driver.findElement(By.name("projectDescription")).sendKeys(projectDescription);
        driver.findElement(By.name("remoteURL")).sendKeys(remoteURL);
        driver.findElement(By.id("submit")).click();
    }
}
