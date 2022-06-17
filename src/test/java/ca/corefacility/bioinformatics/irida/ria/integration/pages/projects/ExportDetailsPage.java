package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class ExportDetailsPage extends AbstractPage {
	@FindBy(id = "t-details-id")
	WebElement detailsId;

	@FindBy(className = "t-upload-status")
	WebElement status;

	public ExportDetailsPage(WebDriver driver) {
		super(driver);
	}

	public static ExportDetailsPage initPage(WebDriver driver) {
		return PageFactory.initElements(driver, ExportDetailsPage.class);
	}

	public String getUploadStatus() {
		return status.getText();
	}

}
