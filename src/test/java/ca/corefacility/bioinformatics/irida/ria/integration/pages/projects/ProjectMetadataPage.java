package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class ProjectMetadataPage extends AbstractPage {
	@FindBy(className = "t-m-field-link")
	WebElement metadataFieldTab;

	@FindBy(className = "t-m-template-link")
	WebElement metadataTemplateLink;

	@FindBy(className = "t-m-field")
	List<WebElement> metadataFieldRow;

	@FindBy(className = "t-m-template")
	List<WebElement> metadataTemplateRow;

	public ProjectMetadataPage(WebDriver driver) {
		super(driver);
	}

	public static ProjectMetadataPage goTo(WebDriver driver) {
		get(driver, "/projects/1/settings/metadata/fields");
		return PageFactory.initElements(driver, ProjectMetadataPage.class);
	}

	public void gotoMetadataFields() {
		metadataFieldTab.click();
		waitForFields();
	}

	public void gotoMetadataTemplates() {
		metadataTemplateLink.click();
		waitForTemplates();
	}

	public int getNumberOfMetadataFields() {
		return metadataFieldRow.size();
	}

	public int getNumberOfMetadataTemplates() {
		return metadataTemplateRow.size();
	}

	private void waitForFields() {
		WebDriverWait wait = new WebDriverWait(driver, 4);
		wait.until(ExpectedConditions.visibilityOfAllElements(metadataFieldRow));
	}

	private void waitForTemplates() {
		WebDriverWait wait = new WebDriverWait(driver, 4);
		wait.until(ExpectedConditions.visibilityOfAllElements(metadataTemplateRow));
	}
}
