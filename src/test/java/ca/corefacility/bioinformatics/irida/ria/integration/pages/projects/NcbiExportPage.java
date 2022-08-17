package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.springframework.web.util.UriUtils;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * Page to test the NCBI export feature
 */
public class NcbiExportPage extends AbstractPage {

	@FindBy(className = "t-sample-panel")
	private List<WebElement> samplePanels;
	public NcbiExportPage(WebDriver driver) {
		super(driver);
	}

	public static NcbiExportPage goToPage(WebDriver driver, long projectId) {
		get(driver, "projects/" + projectId + "/ncbi");
		return PageFactory.initElements(driver, NcbiExportPage.class);
	}

	public int getNumberOfSamples() {
		return samplePanels.size();
	}
}
