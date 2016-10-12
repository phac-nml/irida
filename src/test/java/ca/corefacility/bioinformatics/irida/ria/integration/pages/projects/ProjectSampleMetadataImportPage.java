package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * Created by josh on 2016-10-07.
 */
public class ProjectSampleMetadataImportPage extends AbstractPage {
	@FindBy(id = "dz-form") WebElement dropzone;
	@FindBy(id = "sampleId-previous") WebElement sampleIdPrev;
	@FindBy(css = "input[type=radio]") List<WebElement> headerRadios;
	@FindBy(id = "preview-btn") WebElement previewBtn;
	@FindBy(id = "found-pill") WebElement foundPill;
	@FindBy(id = "missing-pill") WebElement missingPill;

	public ProjectSampleMetadataImportPage(WebDriver driver) {
		super(driver);
	}

	public static ProjectSampleMetadataImportPage goToPage(WebDriver driver) {
		get(driver, "projects/1/sample-metadata");
		return PageFactory.initElements(driver, ProjectSampleMetadataImportPage.class);
	}

	public void uploadMetadataFile(String filePath) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOf(dropzone));
		Path path = Paths.get(filePath);
		dropzone.sendKeys(path.toAbsolutePath().toString());
		waitForTime(500);
	}

	public void selectSampleNameColumn() {
		headerRadios.get(3).click();
		previewBtn.click();
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOf(foundPill));
	}

	public int getFoundCount() {
		return Integer.parseInt(foundPill.findElement(By.className("badge")).getText());
	}
}
