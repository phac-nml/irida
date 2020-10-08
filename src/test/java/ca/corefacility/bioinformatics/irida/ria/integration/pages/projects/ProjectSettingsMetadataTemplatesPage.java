package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class ProjectSettingsMetadataTemplatesPage extends AbstractPage {
	private static final String RELATIVE_URL = "projects/{id}/settings/metadata-templates";

	@FindBy(className = "t-create-template-btn")
	private WebElement createTemplateBtn;

	@FindBy(className = "t-template")
	private List<WebElement> templates;

	public ProjectSettingsMetadataTemplatesPage(WebDriver driver) {
		super(driver);
	}

	public static ProjectSettingsMetadataTemplatesPage goToPage(WebDriver driver, int projectId) {
		get(driver, RELATIVE_URL.replace("{id}", String.valueOf(projectId)));
		return PageFactory.initElements(driver, ProjectSettingsMetadataTemplatesPage.class);
	}

	public int getNumberOfTemplatesInProject() {
		return templates.size();
	}
}
