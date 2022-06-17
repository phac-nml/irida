package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class ExportDetailsPage extends AbstractPage {

	@FindBy(className = "t-upload-status")
	WebElement status;

	@FindBy(className = "t-submitter")
	WebElement submitter;

	@FindBy(className = "t-created-date")
	WebElement createdDate;

	@FindBy(className = "t-bioproject")
	WebElement bioproject;

	@FindBy(className = "t-organization")
	WebElement organization;

	@FindBy(className = "t-namespace")
	WebElement namespace;

	@FindBy(className = "t-release-date")
	WebElement releaseDate;

	@FindBy(className = "t-pair")
	List<WebElement> pairsList;

	@FindBy(className = "t-single")
	List<WebElement> singlesList;

	public ExportDetailsPage(WebDriver driver) {
		super(driver);
	}

	public static ExportDetailsPage initPage(WebDriver driver) {
		return PageFactory.initElements(driver, ExportDetailsPage.class);
	}

	public String getUploadStatus() {
		return status.getText();
	}

	public String getSubmitter() {
		return submitter.getText();
	}

	public String getCreatedDate() {
		return createdDate.getText();
	}

	public String getBioproject() {
		return bioproject.getText();
	}

	public String getOrganization() {
		return organization.getText();
	}

	public String getNamespace() {
		return namespace.getText();
	}

	public String getReleaseDate() {
		return releaseDate.getText();
	}

	public int getNumberOfPairs() {
		return pairsList.size();
	}

	public int getNumberOfSingles() {
		return singlesList.size();
	}
}
