package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ProjectLineListCreateTemplatePage extends ProjectPageBase {
	@FindBy(id = "template-name")
	private WebElement templateNameInput;

	@FindBy(id = "save-template-btn")
	private WebElement saveTemplateBtn;

	@FindBy(id = "existing-templates")
	private WebElement existingTemplatesSelect;

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
}
