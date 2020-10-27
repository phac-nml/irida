package ca.corefacility.bioinformatics.irida.ria.integration.pages.sequenceFiles;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * Created by josh on 14-08-06.
 */
public class SequenceFilePages extends AbstractPage {
	public static final String MAIN_URL = "sequenceFiles/1/file/1/summary";
	public static final String MAIN_URL_NO_DATA = "sequenceFiles/2/file/2/summary";
	public static final String OVERREPRESENTED_URL = "sequenceFiles/1/file/1/summary/overrepresented";
	public static final String DETAILS_URL = "sequenceFiles/1/file/1/summary/details";
	private static final Logger logger = LoggerFactory.getLogger(SequenceFilePages.class);

	public SequenceFilePages(WebDriver driver) {
		super(driver);
	}

	public void goToChartsPage() {
		logger.debug("Going to Sequence File FastQC Charts Page.");
		get(driver, MAIN_URL);
		waitForTime(700);
	}

	public void goToDetailsPageWithNoData() {
		logger.debug("Going to Sequence File FastQC Dashboard Page for file with no fastQC data.");
		get(driver, MAIN_URL_NO_DATA);
		waitForTime(700);
	}

	public void goToOverrepresentedPage() {
		logger.debug("Going to Sequence File FastQC Overrepresented Page.");
		get(driver, OVERREPRESENTED_URL);
	}

	public void goToDetailsPage() {
		logger.debug("Going to Sequence File FastQC details page.");
		get(driver, DETAILS_URL);
	}

	//***********************************************************************************************
	// Lookups for FastQC Details Page
	//***********************************************************************************************

	public String getPageTitle() {
		return driver.findElement(By.className("t-main-heading")).getText();
	}

	public String getFileId() {
		WebElement listItem = driver.findElement(By.className("t-fastqc-id"));
		WebElement listContent = listItem.findElement(By.className("ant-list-item-meta-content"));
		return listContent.findElement(By.className("ant-list-item-meta-description")).getText();
	}

	public String getFileCreatedDate() {
		WebElement listItem = driver.findElement(By.className("t-fastqc-uploaded-on"));
		WebElement listContent = listItem.findElement(By.className("ant-list-item-meta-content"));
		return listContent.findElement(By.className("ant-list-item-meta-description")).getText();
	}

	public String getFileEncoding() {
		WebElement listItem = driver.findElement(By.className("t-fastqc-encoding"));
		WebElement listContent = listItem.findElement(By.className("ant-list-item-meta-content"));
		return listContent.findElement(By.className("ant-list-item-meta-description")).getText();
	}

	public String getTotalSequenceCount() {
		WebElement listItem = driver.findElement(By.className("t-fastqc-total-sequences"));
		WebElement listContent = listItem.findElement(By.className("ant-list-item-meta-content"));
		return listContent.findElement(By.className("ant-list-item-meta-description")).getText();
	}

	public String getTotalBasesCount() {
		WebElement listItem = driver.findElement(By.className("t-fastqc-total-bases"));
		WebElement listContent = listItem.findElement(By.className("ant-list-item-meta-content"));
		return listContent.findElement(By.className("ant-list-item-meta-description")).getText();
	}

	public String getMinLength() {
		WebElement listItem = driver.findElement(By.className("t-fastqc-min-length"));
		WebElement listContent = listItem.findElement(By.className("ant-list-item-meta-content"));
		return listContent.findElement(By.className("ant-list-item-meta-description")).getText();
	}

	public String getMaxLength() {
		WebElement listItem = driver.findElement(By.className("t-fastqc-max-length"));
		WebElement listContent = listItem.findElement(By.className("ant-list-item-meta-content"));
		return listContent.findElement(By.className("ant-list-item-meta-description")).getText();
	}

	public String getGCContent() {
		WebElement listItem = driver.findElement(By.className("t-fastqc-gc-content"));
		WebElement listContent = listItem.findElement(By.className("ant-list-item-meta-content"));
		return listContent.findElement(By.className("ant-list-item-meta-description")).getText();
	}

	public boolean isFastQCLinksVisible() {
		return driver.findElements(By.className("t-fastQC-nav")).size() > 0;
	}

	public boolean isFastQCNoRunWarningDisplayed() {
		return driver.findElements(By.className("t-fastQC-no-run")).size() > 0;
	}

	//***********************************************************************************************
	// Lookups for FastQC Charts Page
	//***********************************************************************************************

	public int getChartCount() {
		return driver.findElements(By.cssSelector(".t-sequenceFile-qc-chart")).size();
	}

	//***********************************************************************************************
	// Lookups for Overrepresented Sequence Page
	//***********************************************************************************************

	public int getNumberOfOverrepresentedSequences() {
		return driver.findElements(By.cssSelector("table tbody tr")).size();
	}
	public String getOverrepresentedSequence() {
		return driver.findElement(By.cssSelector("table tbody tr:nth-child(1) td:nth-child(1)")).getText();
	}
	public String getOverrepresentedSequencePercentage() {
		return driver.findElement(By.cssSelector("table tbody tr:nth-child(1) td:nth-child(2)")).getText();
	}
	public String getOverrepresentedSequenceCount() {
		return driver.findElement(By.cssSelector("table tbody tr:nth-child(1) td:nth-child(3)")).getText();
	}
	public String getOverrepresentedSequenceSource() {
		return driver.findElement(By.cssSelector("table tbody tr:nth-child(1) td:nth-child(4)")).getText();
	}
}
