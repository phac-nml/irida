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

	/**
	 * Open the tab to display the list of files for this analysis.
	 */
	public void displayProvenanceView() {
		tabProvenance.click();
	}


	public boolean priorityEditVisible() {
		return !driver.findElements(By.className("t-priority-edit"))
				.isEmpty();
	}

	/**
	 * Open the tab to display the list of input files for the analysis
	 */
	public void displayInputFilesTab() {
		tabInputFiles.click();
	}

	/**
	 * Open the accordion that contains the tools for the tree.
	 */
	public void displayTreeTools() {
		setCurrentFile();
		this.currentFile.findElement(By.className("accordion-toggle"))
				.click();
	}

	/**
	 * Determine the number of files created.
	 *
	 * @return {@link Integer}
	 */
	public int getNumberOfFilesDisplayed() {
		return fileInfo.size();
	}

	/**
	 * Determine the number of tools used to create the tree.
	 *
	 * @return {@link Integer} count of number of tools.
	 */
	public int getNumberOfToolsForTree() {
		return currentFile.findElements(By.className("tool"))
				.size();
	}

	/**
	 * Determine the number of parameters and their values used in the first
	 * tool
	 *
	 * @return {@link Integer} count of number of parameters
	 */
	public int getNumberOfParametersForTool() {
		waitForElementVisible(By.className("tool"));
		this.currentFile.findElements(By.className("tool"))
				.get(0)
				.click();
		WebElement paramTable = currentFile.findElement(By.className("parameters"));
		return paramTable.findElements(By.className("parameter"))
				.size();
	}

	/**
	 * Sets the current file for use by multiple methods.
	 */
	private void setCurrentFile() {
		this.currentFile = null;
		for (WebElement fileDiv : fileInfo) {
			WebElement filename = fileDiv.findElement(By.className("name"));
			if (filename.getText()
					.contains("tree")) {
				this.currentFile = fileDiv;
				break;
			}
		}
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


	public String getLabelForSample(int index) {
		return sampleLabels.get(index)
				.getText();
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

	public boolean jobErrorAlertVisible() {

		return !driver.findElements(By.className("ant-alert-warning"))
				.isEmpty();
	}
}
