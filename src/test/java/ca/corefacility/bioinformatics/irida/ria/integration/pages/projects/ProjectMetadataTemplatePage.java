package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class ProjectMetadataTemplatePage extends AbstractPage {
	private static final String RELAIVE_URL = "projects/{id}/sample-metadata/template";

	@FindBy(id = "template-name") private WebElement templateNameInput;
	@FindBy(id = "save-template-btn") private WebElement saveTemplateButton;
	@FindBy(id = "add-field-btn") private WebElement addFieldButton;
	@FindBy(className = "ui-select-toggle") private WebElement fieldSelectToggle;
	@FindBy(css = "input[type='search']") private WebElement fieldSearchInput;
	@FindBy(css = ".ui-select-choices li") private List<WebElement> fieldSearchChoices;

	public ProjectMetadataTemplatePage(WebDriver driver) {
		super(driver);
	}

	public static ProjectMetadataTemplatePage goToPage(WebDriver driver, int projectId) {
		get(driver, RELAIVE_URL.replace("{id}", String.valueOf(projectId)));
		return PageFactory.initElements(driver, ProjectMetadataTemplatePage.class);
	}

	public static ProjectMetadataTemplatePage getPage(WebDriver driver) {
		return PageFactory.initElements(driver, ProjectMetadataTemplatePage.class);
	}

	public void setTemplateName(String name) {
		templateNameInput.clear();
		templateNameInput.sendKeys(name);
	}

	public boolean isSaveButtonEnabled() {
		return saveTemplateButton.isEnabled();
	}
}
