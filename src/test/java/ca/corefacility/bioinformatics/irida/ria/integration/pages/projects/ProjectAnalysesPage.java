package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class ProjectAnalysesPage extends AbstractPage {

	public ProjectAnalysesPage(WebDriver driver) {
		super(driver);
	}

	public static ProjectAnalysesPage initializeProjectAnalysesPage(WebDriver driver, int projectId) {
		get(driver, "projects/" + projectId + "/analyses");
		return PageFactory.initElements(driver, ProjectAnalysesPage.class);
	}
}
