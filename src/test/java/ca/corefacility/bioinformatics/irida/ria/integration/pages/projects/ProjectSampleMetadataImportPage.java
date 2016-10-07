package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.util.List;

import org.openqa.selenium.By;
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
	@FindBy(css = "input[type=radio]") List<WebElement> headerRadios;
	@FindBy(id = "preview-btn") WebElement previewBtn;
	@FindBy(id = "found-pill") WebElement foundPill;
	@FindBy(id = "missing-pill") WebElement missingPill;

	public ProjectSampleMetadataImportPage(WebDriver driver) {
		this.driver = driver;
	}

	public void uploadMetadataFile(String path) {
		dzForm.sendKeys(path);
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOf(sampleIdPrev));
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
