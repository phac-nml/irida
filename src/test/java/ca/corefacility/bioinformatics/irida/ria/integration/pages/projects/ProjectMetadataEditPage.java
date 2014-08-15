package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.BasePage;

/**
 * Project Edit Metadata Page
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class ProjectMetadataEditPage {
	public static final String URL = BasePage.URL + "/projects/1/metadata/edit";
	public static final String ATTR_PLACEHOLDER = "placeholder";
    private WebDriver driver;

    public ProjectMetadataEditPage(WebDriver driver) {
        this.driver = driver;
    }

	public void gotoPage() {
		driver.get(URL);
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

	public int getReferenceFileCount() {
		return driver.findElements(By.className("file-name")).size();
	}

	public boolean isDeleteReferenceFileWarningMessageDisplayed() {
		(new WebDriverWait(driver, 10))
				.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className("noty_message")));
		return driver.findElement(By.cssSelector(".noty_message h2")).getText().contains("Removing Reference");
	}

	// ************************************************************************************************
	// EVENTS
	// ************************************************************************************************

	public void clickDeleteReferenceFileButton() {
		driver.findElement(By.className("btn-danger")).click();
		(new WebDriverWait(driver, 10))
				.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className("noty_message")));
	}

	public void clickOKtoDeleteReferenceFile() {
		(new WebDriverWait(driver, 10))
				.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".noty_buttons .btn-primary")));
		driver.findElement(By.cssSelector(".noty_buttons .btn-primary")).click();
	}
}
