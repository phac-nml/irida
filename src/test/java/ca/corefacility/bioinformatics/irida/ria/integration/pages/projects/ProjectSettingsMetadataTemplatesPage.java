package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class ProjectSettingsMetadataTemplatesPage extends AbstractPage {
	private static final String RELATIVE_URL = "projects/{id}/settings/metadata-templates";

	@FindBy(id = "create-template-btn") private WebElement createTemplateBtn;
	@FindBy(id = "template-table") private WebElement templateTable;
	@FindBy(css = "#template-table tbody tr") private List<WebElement> rows;

	public ProjectSettingsMetadataTemplatesPage(WebDriver driver) {
		super(driver);
	}

	public static ProjectSettingsMetadataTemplatesPage goToPage(WebDriver driver, int projectId) {
		get(driver, RELATIVE_URL.replace("{id}", String.valueOf(projectId)));
		return PageFactory.initElements(driver, ProjectSettingsMetadataTemplatesPage.class);
	}

	public int getNumberOfTemplatesInProject() {
		return rows.size();
	}

	public void createNewTemplate() {
		createTemplateBtn.click();
		String previousURL = driver.getCurrentUrl();
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until((ExpectedCondition<Boolean>) input -> (!driver.getCurrentUrl().equals(previousURL)));
	}
}
