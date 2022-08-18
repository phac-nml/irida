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

	@FindBy(xpath = "//*[@id=\"bioProject\"]")
	private WebElement bioProjectInput;

	@FindBy(xpath = "//*[@id=\"organization\"]")
	private WebElement organizationInput;

	@FindBy(xpath = "//*[@id=\"namespace\"]")
	private WebElement namespaceInput;

	public NcbiExportPage(WebDriver driver) {
		super(driver);
	}

	public static NcbiExportPage init(WebDriver driver) {
		return PageFactory.initElements(driver, NcbiExportPage.class);
	}

	public int getNumberOfSamples() {
		return samplePanels.size();
	}

	public void openSamplePanelBySampleName(String sampleName) {
		for (WebElement panel : samplePanels) {
			String text = panel.findElement(By.className("t-sample-name"))
					.getText();
			if (text.equals(sampleName)) {
				panel.click();
				return;
			}
		}
	}

	public void enterBioProject(String value) {
		bioProjectInput.sendKeys(value);
	}

	public void enterOrganization(String value) {
		organizationInput.sendKeys(value);
	}

	public void enterNamespace(String value) {
		namespaceInput.sendKeys(value);
	}
}
