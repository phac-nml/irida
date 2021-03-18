package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class ProjectMetadataPage extends AbstractPage {

	public ProjectMetadataPage(WebDriver driver) {
		super(driver);
	}

	public static ProjectMetadataPage goTo(WebDriver driver) {
		get(driver, "/projects/1/settings/metadata/fields");
		return PageFactory.initElements(driver, ProjectMetadataPage.class);
	}
}
