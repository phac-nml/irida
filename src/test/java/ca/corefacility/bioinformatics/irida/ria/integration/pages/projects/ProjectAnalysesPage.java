package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis.AnalysesUserPage;

public class ProjectAnalysesPage extends AnalysesUserPage {

	public ProjectAnalysesPage(WebDriver driver) {
		super(driver);
	}

	public static ProjectAnalysesPage initializeProjectAnalysesPage(WebDriver driver, int projectId) {
		get(driver, "projects/" + projectId + "/analyses");
		return PageFactory.initElements(driver, ProjectAnalysesPage.class);
	}
}
