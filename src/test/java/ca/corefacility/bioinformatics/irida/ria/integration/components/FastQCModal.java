package ca.corefacility.bioinformatics.irida.ria.integration.components;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class FastQCModal extends AbstractPage {
	@FindBy(className = "t-fastqc-modal-charts-tab")
	private WebElement fastQCModalChartsTab;

	@FindBy(className = "t-fastqc-modal-overrepresented-tab")
	private WebElement fastQCModalOverrepresentedSequencesTab;

	@FindBy(className = "t-fastqc-modal-details-tab")
	private WebElement fastQCModalDetailsTab;

	@FindBy(className = "t-fastqc-modal")
	private WebElement fastqcModal;



	public FastQCModal(WebDriver driver) {
		super(driver);
	}

	public static FastQCModal getFileFastQCDetails(WebDriver driver) {
		return PageFactory.initElements(driver, FastQCModal.class);
	}

	public int getChartCount() {
		List<WebElement> elements = fastqcModal.findElements(By.className("t-sequenceFile-qc-chart"));
		waitForTime(300);
		return elements.size();
	}

	public void closeFastqcModal() {
		WebElement ele = fastqcModal.findElement(By.className("t-fastqc-modal-close"));
		ele.click();
		waitForTime(300);
	}

	public void clickFastQCChartsLink() {
		fastQCModalChartsTab.click();
		waitForTime(300);
	}

	public void clickFastQCOverrepresentedSequencesLink() {
		fastQCModalOverrepresentedSequencesTab.click();
		waitForTime(300);
	}

	public void clickFastQCDetailsLink() {
		fastQCModalDetailsTab.click();
		waitForTime(300);
	}

	public String getFastQCFileTitle() {
		return fastqcModal.findElement(By.className("t-fastqc-modal-file-name")).getText();
	}

	public String getFileId() {
		WebElement listItem = fastqcModal.findElement(By.className("t-fastqc-id"));
		WebElement listContent = listItem.findElement(By.className("ant-list-item-meta-content"));
		return listContent.findElement(By.className("ant-list-item-meta-description")).getText();
	}

	public String getFileCreatedDate() {
		WebElement listItem = fastqcModal.findElement(By.className("t-fastqc-uploaded-on"));
		WebElement listContent = listItem.findElement(By.className("ant-list-item-meta-content"));
		return listContent.findElement(By.className("ant-list-item-meta-description")).getText();
	}

	public String getFileEncoding() {
		WebElement listItem = fastqcModal.findElement(By.className("t-fastqc-encoding"));
		WebElement listContent = listItem.findElement(By.className("ant-list-item-meta-content"));
		return listContent.findElement(By.className("ant-list-item-meta-description")).getText();
	}

	public String getTotalSequenceCount() {
		WebElement listItem = fastqcModal.findElement(By.className("t-fastqc-total-sequences"));
		WebElement listContent = listItem.findElement(By.className("ant-list-item-meta-content"));
		return listContent.findElement(By.className("ant-list-item-meta-description")).getText();
	}

	public String getTotalBasesCount() {
		WebElement listItem = fastqcModal.findElement(By.className("t-fastqc-total-bases"));
		WebElement listContent = listItem.findElement(By.className("ant-list-item-meta-content"));
		return listContent.findElement(By.className("ant-list-item-meta-description")).getText();
	}

	public String getMinLength() {
		WebElement listItem = fastqcModal.findElement(By.className("t-fastqc-min-length"));
		WebElement listContent = listItem.findElement(By.className("ant-list-item-meta-content"));
		return listContent.findElement(By.className("ant-list-item-meta-description")).getText();
	}

	public String getMaxLength() {
		WebElement listItem = fastqcModal.findElement(By.className("t-fastqc-max-length"));
		WebElement listContent = listItem.findElement(By.className("ant-list-item-meta-content"));
		return listContent.findElement(By.className("ant-list-item-meta-description")).getText();
	}

	public String getGCContent() {
		WebElement listItem = fastqcModal.findElement(By.className("t-fastqc-gc-content"));
		WebElement listContent = listItem.findElement(By.className("ant-list-item-meta-content"));
		return listContent.findElement(By.className("ant-list-item-meta-description")).getText();
	}

	public int getNumberOfOverrepresentedSequences() {
		return fastqcModal.findElements(By.cssSelector("table tbody tr")).size();
	}
	public String getOverrepresentedSequence() {
		return fastqcModal.findElement(By.cssSelector("table tbody tr:nth-child(1) td:nth-child(1)")).getText();
	}
	public String getOverrepresentedSequencePercentage() {
		return fastqcModal.findElement(By.cssSelector("table tbody tr:nth-child(1) td:nth-child(2)")).getText();
	}
	public String getOverrepresentedSequenceCount() {
		return fastqcModal.findElement(By.cssSelector("table tbody tr:nth-child(1) td:nth-child(3)")).getText();
	}
	public String getOverrepresentedSequenceSource() {
		return fastqcModal.findElement(By.cssSelector("table tbody tr:nth-child(1) td:nth-child(4)")).getText();
	}
}
