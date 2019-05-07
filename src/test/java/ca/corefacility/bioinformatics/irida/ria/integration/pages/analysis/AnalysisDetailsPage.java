package ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

	@FindBy(css = ".t-paired-end h4 span")
	private List<WebElement> sampleLabels;

	public AnalysisDetailsPage(WebDriver driver) {
		super(driver);
	}

	/**
	 * Initialize the page so that the default {@link WebElement} have been
	 * found.
	 *
	 * @param driver
	 *            {@link WebDriver}
	 * @param analysisId
	 *            Id the the analysis page to view.
	 *
	 * @return The initialized {@link AnalysisDetailsPage}
	 */
	public static AnalysisDetailsPage initPage(WebDriver driver, long analysisId) {
		get(driver, RELATIVE_URL + analysisId);
		return PageFactory.initElements(driver, AnalysisDetailsPage.class);
	}

	/**
	 * Open the tab to display the list of files for this analysis.
	 */
	public void displayProvenanceView() {
		tabProvenance.click();
	}

	public void displayShareTab() {
		tabShare.click();

		waitForElementVisible(By.className("share-project"));
	}

	public void displayInputTab() {
		tabInputFiles.click();
		waitForElementVisible(By.className("paired_end"));
	}

	public List<Long> getSharedProjectIds() {
		return shareCheckboxes.stream().filter(s -> s.isSelected()).map(s -> Long.valueOf(s.getAttribute("value")))
				.collect(Collectors.toList());
	}

	public void clickShareBox(Long id) {
		Optional<WebElement> checkbox = shareCheckboxes.stream()
				.filter(s -> s.getAttribute("value").equals(id.toString())).findFirst();

		if (!checkbox.isPresent()) {
			throw new IllegalArgumentException("share box with id " + id + " doesn't exist");
		}
		checkbox.get().click();
	}

	/**
	 * Click the edit button
	 */
	public void clickEditButton() {
		editButton.click();
		waitForTime(500);
	}

	public boolean priorityEditVisible() {
		return !driver.findElements(By.id("analysis-edit-priority")).isEmpty();
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
		this.currentFile.findElement(By.className("accordion-toggle")).click();
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
		return currentFile.findElements(By.className("tool")).size();
	}

	/**
	 * Determine the number of parameters and their values used in the first
	 * tool
	 *
	 * @return {@link Integer} count of number of parameters
	 */
	public int getNumberOfParametersForTool() {
		waitForElementVisible(By.className("tool"));
		this.currentFile.findElements(By.className("tool")).get(0).click();
		WebElement paramTable = currentFile.findElement(By.className("parameters"));
		return paramTable.findElements(By.className("parameter")).size();
	}

	/**
	 * Sets the current file for use by multiple methods.
	 */
	private void setCurrentFile() {
		this.currentFile = null;
		for (WebElement fileDiv : fileInfo) {
			WebElement filename = fileDiv.findElement(By.className("name"));
			if (filename.getText().contains("tree")) {
				this.currentFile = fileDiv;
				break;
			}
		}
	}

	public int getNumberOfSamplesInAnalysis() {
		return pairedEndElements.size();
	}

	public boolean hasJobErrorInfo() {
		return divHasJobError.size() > 0;
	}

	public String getLabelForSample(int index) {
		return sampleLabels.get(index).getText();
	}
}
