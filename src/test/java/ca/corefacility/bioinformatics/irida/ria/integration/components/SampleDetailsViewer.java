package ca.corefacility.bioinformatics.irida.ria.integration.components;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class SampleDetailsViewer extends AbstractPage {
	@FindBy(className = "t-sample-details-modal")
	private WebElement modal;

	@FindBy(className = "t-concatenate-confirm-modal")
	private WebElement concatenateModal;

	@FindBy(className = "t-sample-details-name")
	private WebElement sampleName;

	@FindBy(className = "t-sample-created-date")
	private WebElement createdDate;

	@FindBy(className = "t-sample-details-metadata-item")
	private List<WebElement> metadataFields;

	@FindBy(id="rc-tabs-0-tab-metadata")
	private WebElement metadataTabLink;

	@FindBy(id="rc-tabs-0-tab-files")
	private WebElement filesTabLink;

	@FindBy(className = "t-upload-sample-files")
	private List<WebElement> dragUploadList;

	@FindBy(className = "t-file-details")
	private List<WebElement> files;

	@FindBy(className = "t-add-new-metadata-btn")
	private List<WebElement> addNewMetadataBtn;

	@FindBy(className = "t-remove-file-btn")
	private List<WebElement> removeFileBtns;

	@FindBy(className = "t-concatenation-checkbox")
	private List<WebElement> concatenationCheckboxes;

	@FindBy(className = "t-concatenate-btn")
	private List<WebElement> concatenateBtn;

	@FindBy(className = "t-file-processing-status")
	private List<WebElement> processingStatuses;

	@FindBy(className = "t-concatenate-confirm")
	private List<WebElement> concatenateConfirmBtn;


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

	public void clickMetadataTabLink() {
		metadataTabLink.click();
		waitForTime(300);
	}

	public void clickFilesTabLink() {
		filesTabLink.click();
		waitForTime(300);
	}

	public boolean fileUploadVisible() {
		return dragUploadList.size() == 1;
	}

	public int numberOfFilesDisplayed() {
		if(files != null) {
			return files.size();
		}
		return 0;
	}

	public boolean addNewMetadataButtonVisible() {
		return addNewMetadataBtn.size() == 1;
	}

	public int removeFileButtonsVisible() {
		if(removeFileBtns != null) {
			return removeFileBtns.size();
		}
		return 0;
	}

	public int concatenationCheckboxesVisible() {

		if(concatenationCheckboxes != null) {
			return concatenationCheckboxes.size();
		}
		return 0;
	}

	public boolean concatenationButtonVisible() {
		return concatenateBtn.size() == 1;
	}

	public void selectFilesToConcatenate() {
		List<WebElement> checkboxes = modal.findElements(By.className("t-concatenation-checkbox"));
		for(WebElement element : checkboxes) {
			element.click();
		}
	}

	public void clickConcatenateBtn() {
		concatenateBtn.get(0).click();
		waitForTime(500);
	}

	public int processingStatusesCount() {
			return processingStatuses.size();
	}

	public int singleEndFileCount() {
		return concatenateModal.findElements(By.className("t-single-end-file")).size();
	}

	public void  enterFileName() {
		concatenateModal.findElement(By.id("t-concat-new-file-name")).sendKeys("NewConcatenatedFile");
	}

	public void clickConcatenateConfirmBtn() {
		concatenateConfirmBtn.get(0).click();
		WebDriverWait wait = new WebDriverWait(driver, 20);
		wait.until(ExpectedConditions.invisibilityOfAllElements(driver.findElements(By.className("ant-notification"))));
//		waitForTime(1000);
	}

}
