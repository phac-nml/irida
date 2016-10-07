package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Created by josh on 2016-10-07.
 */
public class ProjectSampleMetadataImportPage {
	private WebDriver driver;

	@FindBy(id = "dz-form") WebElement dzForm;
	@FindBy(id = "sampleId-previous") WebElement sampleIdPrev;

	public ProjectSampleMetadataImportPage(WebDriver driver) {
		this.driver = driver;
	}

	public void uploadMetadataFile(String path) {
		dzForm.sendKeys(path);
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOf(sampleIdPrev));
	}
}
