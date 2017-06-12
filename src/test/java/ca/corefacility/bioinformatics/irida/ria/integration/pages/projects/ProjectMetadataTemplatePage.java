package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class ProjectMetadataTemplatePage extends AbstractPage {
	private static final String RELAIVE_URL = "projects/{id}/metadata-templates/";
	
	private static final String SAVE_BTN = "save-btn";

	@FindBy(id = "template-name") private WebElement templateNameInput;
	@FindBy(id = SAVE_BTN) private WebElement saveTemplateButton;
	@FindBy(id = "add-field-btn") private WebElement addFieldButton;
	@FindBy(className = "select2-search__field") private WebElement fieldSearchInput;
	@FindBy(className = "template-field") private List<WebElement> templateFieldItems;
	@FindBy(id = "template-id") private WebElement templateIdentifier;

	public ProjectMetadataTemplatePage(WebDriver driver) {
		super(driver);
	}

	public static ProjectMetadataTemplatePage goToPage(WebDriver driver, int projectId, String pageName) {
		String url = RELAIVE_URL.replace("{id}", String.valueOf(projectId));
		get(driver, url + pageName);
		return PageFactory.initElements(driver, ProjectMetadataTemplatePage.class);
	}

	public static ProjectMetadataTemplatePage getPage(WebDriver driver) {
		return PageFactory.initElements(driver, ProjectMetadataTemplatePage.class);
	}

	public String getTemplateName() {
		return templateNameInput.getAttribute("value");
	}

	public void setTemplateName(String name) {
		templateNameInput.clear();
		templateNameInput.sendKeys(name);
		templateNameInput.sendKeys(Keys.TAB);
	}

	public boolean isSaveButtonEnabled() {
		return saveTemplateButton.isEnabled();
	}
	
	public boolean isSaveButtonVisible() {
		List<WebElement> findElements = driver.findElements(By.id(SAVE_BTN));
		return !findElements.isEmpty();
	}

	public void addMetadataField(String field) {
		new WebDriverWait(driver, 10);
		addFieldButton.click();
		fieldSearchInput.sendKeys(field);
		waitForTime(500);
		fieldSearchInput.sendKeys(Keys.ENTER);
		waitForTime(1000);
	}

	public int getNumberOfTemplateFields() {
		return templateFieldItems.size();
	}

	public void removeTemplateFieldByIndex(int index) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		WebElement removeBtn = templateFieldItems.get(index).findElement(By.className("field-remove"));
		removeBtn.click();
		wait.until(ExpectedConditions.stalenessOf(removeBtn));
	}

	public void saveTemplate() {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		waitForTime(1000);
		saveTemplateButton.click();
		wait.until(ExpectedConditions.urlMatches("metadata-templates/\\d+"));
	}
}
