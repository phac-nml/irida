package ca.corefacility.bioinformatics.irida.ria.integration.pages.sequenceFiles;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * Created by josh on 14-08-06.
 */
public class SequenceFilePages extends AbstractPage {
	public static final String RELATIVE_URL = "sequenceFiles/1/file/1/summary";
	public static final String RELATIVE_URL_NO_DATA = "sequenceFiles/2/file/2/summary";
	public static final String OVERREPRESENTED_URL = "sequenceFiles/1/file/1/overrepresented";
	private static final Logger logger = LoggerFactory.getLogger(SequenceFilePages.class);

	public SequenceFilePages(WebDriver driver) {
		super(driver);
	}

	public void goToDetailsPage() {
		logger.debug("Going to Sequence File Dashboard Page.");
		get(driver, RELATIVE_URL);
		waitForTime(700);
	}

	public void goToDetailsPageWithNoData() {
		logger.debug("Going to Sequence File Dashboard Page for file with no fastQC data.");
		get(driver, RELATIVE_URL_NO_DATA);
		waitForTime(700);
	}

	public void goToOverrepresentedPage() {
		logger.debug("Going to Sequence File Overrepresented Page.");
		get(driver, OVERREPRESENTED_URL);
	}

	//***********************************************************************************************
	// LOOK UPS FOR PAGE CHROME
	//***********************************************************************************************

	public String getPageTitle() {
		return driver.findElement(By.tagName("h1")).getText();
	}

	public String getFileId() {
		return driver.findElement(By.id("file-id")).getText();
	}

	public String getFileCreatedDate() {
		return driver.findElement(By.id("file-created")).getText();
	}

	public String getFileEncoding() {
		return driver.findElement(By.id("file-encoding")).getText();
	}

	public String getTotalSequenceCount() {
		return driver.findElement(By.id("totalSequences")).getText();
	}

	public String getTotalBasesCount() {
		return driver.findElement(By.id("totalBases")).getText();
	}

	public String getMinLength() {
		return driver.findElement(By.id("minLength")).getText();
	}

	public String getMaxLength() {
		return driver.findElement(By.id("maxLength")).getText();
	}

	public String getGCContent() {
		return driver.findElement(By.id("gcContent")).getText();
	}

	public boolean isFastQCLinksVisible() {
		return driver.findElements(By.id("fastQC-details-nav")).size() > 0;
	}

	public boolean isFastQCDetailsVisisble() {
		return driver.findElements(By.id("fastQC-details")).size() > 0;
	}

	public boolean isFastQCNoRunWarningDisplayed() {
		return driver.findElements(By.id("fastQC-no-run")).size() > 0;
	}

	//***********************************************************************************************
	// Lookups for Details Page
	//***********************************************************************************************

	public int getChartCount() {
		return driver.findElements(By.cssSelector(".sequenceFile img")).size();
	}

	//***********************************************************************************************
	// Lookups for Overrepresented Sequence Page
	//***********************************************************************************************

	public int getNumberOfOverrepresentedSequences() {
		return driver.findElements(By.cssSelector("#orTable tbody tr")).size();
	}
	public String getOverrepresentedSequence() {
		return driver.findElement(By.cssSelector("#orTable tbody tr:nth-child(1) td:nth-child(1)")).getText();
	}
	public String getOverrepresentedSequencePercentage() {
		return driver.findElement(By.cssSelector("#orTable tbody tr:nth-child(1) td:nth-child(2)")).getText();
	}
	public String getOverrepresentedSequenceCount() {
		return driver.findElement(By.cssSelector("#orTable tbody tr:nth-child(1) td:nth-child(3)")).getText();
	}
	public String getOverrepresentedSequenceSource() {
		return driver.findElement(By.cssSelector("#orTable tbody tr:nth-child(1) td:nth-child(4)")).getText();
	}
}
