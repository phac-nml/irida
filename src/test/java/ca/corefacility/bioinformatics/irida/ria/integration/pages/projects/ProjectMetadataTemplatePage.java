package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class ProjectMetadataTemplatePage extends AbstractPage {
	private static final Logger logger = LoggerFactory.getLogger(ProjectMetadataTemplatePage.class);
	private static final String RELAIVE_URL = "projects/{id}/sample-metadata/template";

	@FindBy(id = "template-name") private WebElement templateNameInput;
	@FindBy(id = "save-template-btn") private WebElement saveTemplateButton;
	@FindBy(id = "add-field-btn") private WebElement addFieldButton;
	@FindBy(className = "ui-select-toggle") private WebElement fieldSelectToggle;
	@FindBy(css = "input.ui-select-search") private WebElement fieldSearchInput;
	@FindBy(css = ".ui-select-choices li") private List<WebElement> fieldSearchChoices;
	@FindBy(className = "field-label") private List<WebElement> templateFieldLabels;

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

	public void addMetadataField(String field) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		addFieldButton.click();
		wait.until(ExpectedConditions.visibilityOf(fieldSelectToggle));
		fieldSelectToggle.click();
		logger.debug("---> CLICKED TO GET INPUT FIELD");
		wait.until(ExpectedConditions.visibilityOf(fieldSearchInput));
		logger.debug("---> GOT INPUT FIELD, SENDING SEARCH VALUE");
		fieldSearchInput.sendKeys(field);
		wait.until(ExpectedConditions.visibilityOfAllElements(fieldSearchChoices));
		logger.debug("---> SENT FIELD NAME TO INPUT");
		fieldSearchChoices.get(0).click();
		wait.until(ExpectedConditions.visibilityOfAllElements(templateFieldLabels));
		logger.debug("---> SELECTED FIRST FIELD");
	}

	public int getNumberOfTemplateFields() {
		return templateFieldLabels.size();
	}
}
