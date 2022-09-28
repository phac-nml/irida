package ca.corefacility.bioinformatics.irida.ria.integration.components;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
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

	@FindBy(className = "t-sample-details-project-name")
	private WebElement projectName;

	@FindBy(className = "t-sample-created-date")
	private WebElement createdDate;

	@FindBy(className = "t-sample-details-metadata-item")
	private List<WebElement> metadataFields;

	@FindBy(xpath="//ul[contains(@class, 't-sample-viewer-nav')]/li[2]")
	private WebElement metadataTabLink;

	@FindBy(xpath = "//ul[contains(@class, 't-sample-viewer-nav')]/li[3]")
	private WebElement filesTabLink;


	@FindBy(xpath = "//ul[contains(@class, 't-sample-viewer-nav')]/li[4]")
	private WebElement sampleAnalysesTabLink;

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

	@FindBy(className = "t-download-file-btn")
	private List<WebElement> downloadFileBtns;

	@FindBy(className = "t-file-label")
	private List<WebElement> fileLabels;

	@FindBy(id="t-remove-originals-true")
	private WebElement removeOriginalRadioButton;

	@FindBy(className = "t-remove-file-confirm-btn")
	private List<WebElement> confirmBtns;

	@FindBy(className = "t-set-default-seq-obj-button")
	private List<WebElement> setDefaultSeqObjBtns;

	@FindBy(className = "t-default-seq-obj-tag")
	private List<WebElement> defaultSeqObjTags;

	@FindBy(className = "t-set-default-genome-assembly-button")
	private List<WebElement> setDefaultGenomeAssemblyBtns;

	@FindBy(className = "t-default-genome-assembly-tag")
	private List<WebElement> defaultGenomeAssemblyTags;

	@FindBy(className= "t-sample-analyses")
	private WebElement sampleAnalysesTable;

	@FindBy(className= "t-sample-analyses-search-input")
	private WebElement sampleAnalysesSearchInput;

	@FindBy(className = "ant-list-item")
	private List<WebElement> sampleAnalysesList;

	@FindBy(className = "t-add-sample-to-cart")
	private WebElement addSampleToCartBtn;

	@FindBy(className = "t-remove-sample-from-cart")
	private WebElement removeSampleFromCartBtn;

	@FindBy(className = "t-actions-menu")
	private List<WebElement> actionBtns;


	public SampleDetailsViewer(WebDriver driver) {
		super(driver);
	}
	private String concatenatedFileName = "NewConcatenatedFile";

	public static SampleDetailsViewer getSampleDetails(WebDriver driver) {
		return PageFactory.initElements(driver, SampleDetailsViewer.class);
	}

	public String getSampleName() {
		return sampleName.getText();
	}

	public String getProjectName() {
		return projectName.getText();
	}

	public String getCreatedDateForSample() {
		return createdDate.getText();
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

	public void clickSampleAnalysesTabLink() {
		sampleAnalysesTabLink.click();
		waitForTime(300);
	}

	public boolean fileUploadVisible() {
		return dragUploadList.size() == 1;
	}

	public int numberOfFilesDisplayed() {
		waitForTime(300);
		if(files != null) {
			return files.size();
		}
		return 0;
	}

	public boolean addNewMetadataButtonVisible() {
		return addNewMetadataBtn.size() == 1;
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

	public void selectFilesToConcatenate(int maxCheckboxesToSelect) {
		List<WebElement> checkboxes = modal.findElements(By.className("t-concatenation-checkbox"));
		if(checkboxes.size() > 0 && maxCheckboxesToSelect < checkboxes.size()) {
			checkboxes = checkboxes.subList(0,maxCheckboxesToSelect);
			for(WebElement element : checkboxes) {
				element.click();
			}
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
		concatenateModal.findElement(By.id("t-concat-new-file-name")).sendKeys(concatenatedFileName);
	}

	public void  enterFileName(String filename) {
		concatenateModal.findElement(By.id("t-concat-new-file-name")).sendKeys(filename);
	}

	public void clickConcatenateConfirmBtn() {
		concatenateConfirmBtn.get(0).click();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20L));
		wait.until(ExpectedConditions.invisibilityOfAllElements(driver.findElements(By.className("ant-notification"))));
		waitForTime(500);
	}

	public int actionButtonsVisible() {
		if(actionBtns != null) {
			return actionBtns.size();
		}
		return 0;
	}

	public boolean correctFileNamesDisplayedAdmin(List<String> existingFileNames) {
		boolean correctNames = true;

		for (WebElement fileLabel : fileLabels) {
			String text = fileLabel.getAttribute("innerHTML");
			if (!existingFileNames.contains(text) ) {
				if(!text
						.equals(concatenatedFileName + ".fastq")){
					correctNames = false;
				}
			}
		}
		return correctNames;
	}

	public boolean correctFileNamesDisplayedAdmin(List<String> existingFileNames, String nameOfConcatenatedFile) {
		boolean correctNames = true;

		for (WebElement fileLabel : fileLabels) {
			String text = fileLabel.getAttribute("innerHTML");
			if (!existingFileNames.contains(text) ) {
				if(!text
						.equals(nameOfConcatenatedFile + ".fastq")){
					correctNames = false;
				}
			}
		}
		return correctNames;
	}

	public boolean correctFileNamesDisplayedUser(List<String> existingFileNames) {
		boolean correctNames = true;

		for (WebElement fileLabel : fileLabels) {
			String text = fileLabel.getAttribute("innerHTML");
			if (!existingFileNames.contains(text)) {
				correctNames = false;
			}
		}
		return correctNames;
	}

	public void clickRemoveOriginalsRadioButton() {
		concatenateModal.findElement(By.className("t-remove-originals-true")).click();
		waitForTime(500);
	}

	public void removeFile(int index) {
		actionBtns.get(index).click();
		waitForTime(500);
		removeFileBtns.get(0).click();
		waitForTime(500);
		confirmBtns.get(0).click();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30L));
		wait.until(ExpectedConditions.invisibilityOfAllElements(driver.findElements(By.className("ant-notification"))));
	}

	public boolean setSetDefaultSeqObjButtonsVisible() {
		return setDefaultSeqObjBtns.size() > 0;
	}

	public int defaultSeqObjTagCount() {
		return defaultSeqObjTags.size();
	}

	public void updateDefaultSequencingObjectForSample() {
		setDefaultSeqObjBtns.get(0).click();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20L));
		wait.until(ExpectedConditions.invisibilityOfAllElements(driver.findElements(By.className("ant-notification"))));
	}

	public boolean searchSampleAnalysesInputVisible() {
		return sampleAnalysesSearchInput.isDisplayed();
	}

	public boolean sampleAnalysesTableVisible() {
		return sampleAnalysesTable.isDisplayed();
	}

	public int numberOfSampleAnalysesVisible() {
		return sampleAnalysesTable.findElements(By.className("ant-list-item")).size();
	}

	public int filterSampleAnalyses(String searchString) {
		WebElement searchInput = sampleAnalysesSearchInput.findElement(By.className("ant-input"));
		searchInput.sendKeys(searchString);
		waitForTime(500);
		return sampleAnalysesList.size();
	}

	public void clearSampleAnalysesFilter() {
		WebElement searchInput = sampleAnalysesSearchInput.findElement(By.className("ant-input"));
		searchInput.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
		waitForTime(500);
	}


	public void clickSampleName() {
		WebElement ele = driver.findElement(By.className("t-file-label"));
		ele.click();
	}

	public boolean isAddSampleToCartButtonVisible() {
		try {
			return addSampleToCartBtn.isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isRemoveSampleFromCartButtonVisible() {
		try {
			return removeSampleFromCartBtn.isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	public void clickAddSampleToCartButton() {
		addSampleToCartBtn.click();
		waitForTime(500);
	}

	public void clickRemoveSampleFromCartButton() {
		removeSampleFromCartBtn.click();
		waitForTime(500);
	}

	public int numberOfSequencingObjectsSetAsDefault() {
		return defaultSeqObjTags.size();
	}

	public int numberOfGenomeAssembliesSetAsDefault() {
		return defaultGenomeAssemblyTags.size();
	}

	public int numberOfSetAsDefaultSeqObjsButtons() {
		return setDefaultSeqObjBtns.size();
	}

	public int numberOfGenomeAssembliesSetAsDefaultButtons() {
		return setDefaultGenomeAssemblyBtns.size();
	}

	public boolean sampleDetailsViewerVisible() {
		try {
			return modal.isDisplayed();
		} catch(Exception e) {
			return false;
		}
	}

}
