package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.PageUtilities;

/**
 * Project Edit Metadata Page
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class ProjectMetadataEditPage extends AbstractPage {
	public static final String RELATIVE_URL = "projects/1/metadata/edit";
	public static final String ATTR_PLACEHOLDER = "placeholder";
	private PageUtilities pageUtilities;

	public ProjectMetadataEditPage(WebDriver driver) {
		super(driver);
		this.pageUtilities = new PageUtilities(driver);
	}

	public void gotoPage() {
		get(driver, RELATIVE_URL);
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
		waitForTime(100);
		pageUtilities.waitForElementVisible(By.className("noty_message"));
		return driver.findElement(By.cssSelector(".noty_text h2")).getText().contains("Removing Reference");
	}

	// ************************************************************************************************
	// EVENTS
	// ************************************************************************************************

	public void clickDeleteReferenceFileButton() {
		pageUtilities.waitForElementVisible(By.className("file-delete-btn"));
		driver.findElement(By.className("file-delete-btn")).click();
		pageUtilities.waitForElementVisible(By.className("noty_message"));
	}

	public void clickOKtoDeleteReferenceFile() {
		pageUtilities.waitForElementVisible(By.id("button-0"));
		WebElement el = driver.findElement(By.id("button-0"));
		el.click();
	}

	public void clickReferenceFilesTab() {
		driver.findElement(By.cssSelector("ul.nav-tabs li:nth-child(2) a")).click();
		pageUtilities.waitForElementVisible(By.className("file-delete-btn"));
	}
}
