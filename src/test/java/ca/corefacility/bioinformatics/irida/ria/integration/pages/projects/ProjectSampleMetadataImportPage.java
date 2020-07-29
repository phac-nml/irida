package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

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
	@FindBy(id = "file-upload-button") WebElement dropzone;
	@FindBy(id = "sampleId-previous") WebElement sampleIdPrev;
	@FindBy(css = "input[type=radio]") List<WebElement> headerRadios;
	@FindBy(id = "preview-btn") WebElement previewBtn;
	@FindBy(id = "found-pill") WebElement foundPill;
	@FindBy(id = "missing-pill") WebElement missingPill;
	@FindBy(css = "thead th") List<WebElement> headers;
	@FindBy(css = "tbody tr") List<WebElement> rows;

	public ProjectSampleMetadataImportPage(WebDriver driver) {
		super(driver);
	}

	public static ProjectSampleMetadataImportPage goToPage(WebDriver driver) {
		get(driver, "projects/1/sample-metadata/upload");
		return PageFactory.initElements(driver, ProjectSampleMetadataImportPage.class);
	}

	public void uploadMetadataFile(String filePath) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		Path path = Paths.get(filePath);
		dropzone.sendKeys(path.toAbsolutePath().toString());
		wait.until(ExpectedConditions.visibilityOf(sampleIdPrev));
	}

	public void selectSampleNameColumn() {
		headerRadios.get(3).click();
		previewBtn.click();
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOf(foundPill));
	}

	public int getFoundCount() {
		return Integer.parseInt(foundPill.findElement(By.className("badge"))
				.getText());
	}

	public int getMissingCount() {
		return Integer.parseInt(missingPill.findElement(By.className("badge"))
				.getText());
	}

	public List<String> getValuesForColumnByName(String column) {
		// Get the text from the headers
		List<String> headerText = headers.stream()
				.map(WebElement::getText)
				.collect(Collectors.toList());
		// Find which columns is the numbers
		int index = headerText.indexOf(column);
		return rows.stream()
				.map(row -> row.findElements(By.tagName("td"))
						.get(index)
						.getText())
				.collect(Collectors.toList());
	}
}
