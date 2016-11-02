package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ProjectLineListCreateTemplatePage extends ProjectPageBase {
	@FindBy(id = "template-name")
	private WebElement templateNameInput;

	@FindBy(id = "save-template-btn")
	private WebElement saveTemplateBtn;

	@FindBy(className = "select2-selection__rendered")
	private WebElement existingTemplatesSelect;

	@FindBy(className = "select2-search__field")
	private WebElement existTemplateField;

	@FindBy(className = "entry")
	private List<WebElement> fieldEntries;

	public ProjectLineListCreateTemplatePage(WebDriver driver) {
		super(driver);
	}

	public static ProjectLineListCreateTemplatePage goToPage(WebDriver driver) {
		get(driver, "/projects/1/linelist/linelist-templates");
		return PageFactory.initElements(driver, ProjectLineListCreateTemplatePage.class);
	}

	public boolean isSaveBtnEnabled() {
		return saveTemplateBtn.isEnabled();
	}

	public void setNewTemplateName(String name) {
		templateNameInput.clear();
		templateNameInput.sendKeys(name);
	}

	public void addExistingTemplate(String templateName) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		existingTemplatesSelect.click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("select2-selection__rendered")));
		existTemplateField.sendKeys(templateName, Keys.ENTER);
		// Wait for page to update
		waitForTime(500);
	}

	public int getNumberOfFields() {
		return fieldEntries.size();
	}
}
