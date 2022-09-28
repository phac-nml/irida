package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.time.Duration;
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

	@FindBy(className = "ui-select-search")
	private WebElement existingTemplatesSelect;

	@FindBy(className = "entry")
	private List<WebElement> fieldEntries;

	@FindBy(className = "remove-field-btn")
	private List<WebElement> removeFieldBtns;

	@FindBy(id = "add-field-btn")
	private WebElement addFieldBtn;

	public ProjectLineListCreateTemplatePage(WebDriver driver) {
		super(driver);
	}

	public static ProjectLineListCreateTemplatePage goToPage(WebDriver driver) {
		get(driver, "/projects/1/sample-metadata/templates/new");
		return PageFactory.initElements(driver, ProjectLineListCreateTemplatePage.class);
	}

	public boolean isSaveBtnEnabled() {
		return saveTemplateBtn.isEnabled();
	}

	public void setNewTemplateName(String name) {
		templateNameInput.clear();
		templateNameInput.sendKeys(name);
		waitForTime(500);
	}

	public void addExistingTemplate(String templateName) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		existingTemplatesSelect.click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ui-select-choices-row")));
		existingTemplatesSelect.sendKeys(templateName, Keys.ENTER);
		// Wait for page to update
		waitForTime(500);
	}

	public void addNewField() {
		addFieldBtn.click();
		// wait for field to be added
		waitForTime(600);
	}

	public void setFieldLabel(int item, String label) {
		WebElement element = driver.findElements(By.className("entry")).get(item);
		element.findElement(By.cssSelector("input[type=text]")).sendKeys(label);
	}

	public void removeField(int field) {
		removeFieldBtns.get(field).click();
		waitForTime(100);
	}

	public void saveNewTemplate(String templateName) {
		saveTemplateBtn.click();
		waitForTime(1000);
	}

	public int getNumberOfFields() {
		return fieldEntries.size();
	}
}
