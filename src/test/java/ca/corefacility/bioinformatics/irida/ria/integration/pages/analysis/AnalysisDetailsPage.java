package ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class AnalysisDetailsPage extends AbstractPage {
	public static final String RELATIVE_URL = "analysis/";

	@FindBy(id = "analysis-download-btn")
	private WebElement analysisDownloadBtn;

	@FindBy(id = "tab-preview")
	private WebElement tabPreview;

	@FindBy(id = "tab-files")
	private WebElement tabFiles;

	@FindBy(className = "filename")
	private List<WebElement> filenames;

	public AnalysisDetailsPage(WebDriver driver) {
		super(driver);
	}

	public static AnalysisDetailsPage initPage(WebDriver driver, int analysisId) {
		get(driver, RELATIVE_URL + analysisId);
		return PageFactory.initElements(driver, AnalysisDetailsPage.class);
	}

	public void displayFilesView() {
		tabFiles.click();
	}

	public int getNumberOfFilesDisplayed() {
		return filenames.size();
	}
}
