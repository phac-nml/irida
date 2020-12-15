package ca.corefacility.bioinformatics.irida.ria.integration.components;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class SampleDetailsViewer extends AbstractPage {
	@FindBy(className = "t-sample-details-modal")
	private WebElement modal;

	@FindBy(className = "t-sample-details-name")
	private WebElement sampleName;

	@FindBy(className = "t-date-text")
	private WebElement createdDate;

	@FindBy(className = "t-sample-details-metadata-item")
	private List<WebElement> metadataFields;

	public SampleDetailsViewer(WebDriver driver) {
		super(driver);
	}

	public static SampleDetailsViewer getSampleDetails(WebDriver driver) {
		return PageFactory.initElements(driver, SampleDetailsViewer.class);
	}

	public String getSampleName() {
		return sampleName.getText();
	}

	public String getCreatedDateForSample() {
		return createdDate.getText();
	}

	public void closeDetails() {
		modal.findElement(By.className("ant-modal-close"))
				.click();
	}

	public int getNumberOfMetadataEntries() {
		return metadataFields.size();
	}

	public String getValueForMetadataField(String label) {
		for (WebElement field : metadataFields) {
			if (field.findElement(By.className("t-sample-details-metadata__field"))
					.getText()
					.equals(label)) {
				return field.findElement(By.className("t-sample-details-metadata__entry"))
						.getText();
			}
		}
		return null;
	}
}
