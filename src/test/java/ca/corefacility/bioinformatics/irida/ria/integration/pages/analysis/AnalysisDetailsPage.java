package ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class AnalysisDetailsPage extends AbstractPage {
	public static final String RELATIVE_URL = "analysis/";

	@FindBy(id = "analysis-download-btn")
	private WebElement analysisDownloadBtn;

	@FindBy(id = "preview")
	private WebElement tabPreview;

	@FindBy(id = "provenance")
	private WebElement tabProvenance;

	@FindBy(id = "inputs")
	private WebElement tabInputFiles;

	@FindBy(id = "share")
	private WebElement tabShare;

	@FindBy(className = "file-info")
	private List<WebElement> fileInfo;

	@FindBy(className = "t-file-header")
	private List<WebElement> files;

	@FindBy(className = "t-file-name")
	private List<WebElement> fileNames;

	@FindBy(className = "t-galaxy-parameter")
	private List<WebElement> galaxyParameters;

	@FindBy(className = "t-tool-name")
	private List<WebElement> toolList;

	@FindBy(className = "share-project")
	List<WebElement> shareCheckboxes;

	@FindBy(className = "t-paired-end")
	private List<WebElement> pairedEndElements;

	@FindBy(id = "editAnalysisButton")
	private WebElement editButton;

	private WebElement currentFile;

	@FindBy(className = "it-has-job-error")
	private List<WebElement> divHasJobError;

	@FindBy(className = "t-paired-end-sample-name")
	private List<WebElement> sampleLabels;

	@FindBy(id = "t-analysis-tab-settings")
	private WebElement settingsTab;

	@FindBy(id = "root")
	private WebElement rootDiv;

	@FindBy(className = "ant-popover-inner-content")
	private WebElement confirmDiv;

	@FindBy(id = "t-delete-analysis-btn")
	private WebElement deleteButton;

	@FindBy(id="t-sample-search-input")
	private WebElement searchInput;

	public AnalysisDetailsPage(WebDriver driver) {
		super(driver);
	}

	/**
	 * Initialize the page so that the default {@link WebElement} have been
	 * found.
	 *
	 * @param driver     {@link WebDriver}
	 * @param analysisId Id the the analysis page to view.
	 * @param tabKey     Tab to be active on load
	 * @return The initialized {@link AnalysisDetailsPage}
	 */
	public static AnalysisDetailsPage initPage(WebDriver driver, long analysisId, String tabKey) {
		get(driver, RELATIVE_URL + analysisId + "/" + tabKey);
		return PageFactory.initElements(driver, AnalysisDetailsPage.class);
	}


	public boolean priorityEditVisible() {
		return !driver.findElements(By.className("t-priority-edit"))
				.isEmpty();
	}


	/**
	 * Determine the number of files created.
	 *
	 * @return {@link Integer}
	 */
	public int getNumberOfFilesDisplayed() {
		return fileNames.size();
	}

	public int getNumberOfSamplesInAnalysis() {
		return pairedEndElements.size();
	}

	public boolean comparePageTitle(String pageTitle) {
		int titleFound = rootDiv.findElements(By.xpath("//span[contains(text(),'" + pageTitle + "')]"))
				.size();

		if (titleFound > 0) {
			return true;
		}

		return false;
	}

	public boolean compareTabTitle(String pageTitle) {
		int titleFound = rootDiv.findElements(By.xpath("//span[contains(text(),'" + pageTitle + "')]"))
				.size();

		if (titleFound > 0) {
			return true;
		}

		return false;
	}


	public boolean emailPipelineResultVisible() {
		return !driver.findElements(By.className("t-email-pipeline-result"))
				.isEmpty();
	}

	public boolean deleteButtonExists() {
		return rootDiv.findElements(By.className("ant-btn-danger")).size() > 0;
	}

	public void deleteAnalysis() {
		rootDiv.findElements(By.className("ant-btn-danger")).get(0).click();
		waitForTime(500);
		waitForElementVisible(By.className("ant-popover-inner-content"));
		confirmDiv.findElements(By.className("ant-btn-sm")).get(1).click();
	}

	public boolean hasSharedWithProjects() {
		return rootDiv.findElements(By.className("ant-checkbox-checked")).size() > 0;
	}

	public void removeSharedProjects() {
		rootDiv.findElements(By.className("ant-checkbox-checked")).get(0).click();
	}

	public int getNumberOfListItems() {
		return rootDiv.findElements(By.className("ant-list-item-meta-title")).size();
	}

	public int getNumberOfListItemValues() {
		return rootDiv.findElements(By.className("ant-list-item-meta-description")).size();
	}

	public boolean hasSideBarTabLinks() {
		return rootDiv.findElements(By.className("ant-layout-has-sider")).size() == 1;
	}

	public boolean hasHorizontalTabLinks() {
		return rootDiv.findElements(By.className("ant-menu-horizontal")).size() == 1;
	}

	public boolean jobErrorAlertVisible() {

		return !driver.findElements(By.className("ant-alert-warning"))
				.isEmpty();
	}

	public int getProvenanceFileCount() {
		return files.size();
	}

	public int getGalaxyParametersCount() {
		return galaxyParameters.size();
	}

	public int getToolCount() {
		return toolList.size();
	}

	public void getFileProvenance() {
		files.get(0).click();
	}

	public void displayToolExecutionParameters() {
		toolList.get(0).click();
	}

	public void filterSamples(String searchStr) {
		searchInput.sendKeys(searchStr);
		waitForTime(500);
	}
}
