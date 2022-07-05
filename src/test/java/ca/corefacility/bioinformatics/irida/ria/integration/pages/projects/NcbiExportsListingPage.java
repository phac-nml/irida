package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class NcbiExportsListingPage extends AbstractPage {
	@FindBy(className = "t-biosample-id")
	private List<WebElement> biodamplesIds;

	public NcbiExportsListingPage(WebDriver driver) {
		super(driver);
	}

	public static NcbiExportsListingPage goTo(WebDriver driver) {
		get(driver, "projects/1/export");
		return PageFactory.initElements(driver, NcbiExportsListingPage.class);
	}

	public int getNumberOfBioSampleIdsDisplayed() {
		return biodamplesIds.size();
	}
}
