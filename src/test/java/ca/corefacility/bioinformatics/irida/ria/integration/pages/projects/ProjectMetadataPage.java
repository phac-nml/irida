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

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class ProjectMetadataPage extends AbstractPage {
	@FindBy(className = "t-m-field-link")
	WebElement metadataFieldTab;

	@FindBy(className = "t-m-template-link")
	WebElement metadataTemplateLink;

	@FindBy(className = "t-m-field")
	List<WebElement> metadataFieldRow;

	@FindBy(className = "t-create-template")
	WebElement createTemplateButton;

	@FindBy(className = "t-create-modal")
	WebElement createTemplateModal;

	@FindBy(className = "t-m-template")
	List<WebElement> metadataTemplateRow;

	@FindBy(className = "t-t-header-name")
	WebElement metadataTemplateName;

	@FindBy(className = "t-t-edit-name")
	WebElement templateEditName;

	@FindBy(className = "t-field-restriction")
	List<WebElement> fieldRestrictions;

	public ProjectMetadataPage(WebDriver driver) {
		super(driver);
	}

	public static ProjectMetadataPage goTo(WebDriver driver) {
		get(driver, "projects/1/settings/metadata/fields");
		return PageFactory.initElements(driver, ProjectMetadataPage.class);
	}

	public void gotoMetadataFields() {
		metadataFieldTab.click();
		waitForFields();
	}

	public void gotoMetadataTemplates() {
		metadataTemplateLink.click();
		waitForTemplates();
	}

	public int getNumberOfMetadataFields() {
		return metadataFieldRow.size();
	}

	public void selectMetadataField(String name) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(4));
		for (WebElement row : metadataFieldRow) {
			String text = row.findElement(By.className("t-m-field-label")).getText();
			if (text.equalsIgnoreCase(name)) {
				WebElement checkbox = row.findElement(By.className("ant-checkbox-input"));
				checkbox.click();
				wait.until(ExpectedConditions.elementToBeSelected(checkbox));
			}
		}
	}

	public boolean isCreateTemplateButtonVisible() {
		return driver.findElements(By.className("t-create-template")).size() == 1;
	}

	public void createNewTemplate(String name, String description) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(4));
		createTemplateButton.click();
		wait.until(ExpectedConditions.visibilityOf(createTemplateModal));
		createTemplateModal.findElement(By.className("t-c-t-name")).sendKeys(name);
		createTemplateModal.findElement(By.className("t-c-t-desc")).sendKeys(description);
		createTemplateModal.findElement(By.className("t-create-modal-ok")).click();
		wait.until(ExpectedConditions.urlContains("/metadata/templates"));
	}

	public void gotoTemplate(String name) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(4));
		for (WebElement row : metadataTemplateRow) {
			WebElement element = row.findElement(By.className("t-t-name"));
			if (element.getText().equalsIgnoreCase(name)) {
				element.click();
				wait.until(ExpectedConditions.urlContains("/metadata/templates/"));
				break;
			}
		}
	}

	public boolean canDeleteTemplate() {
		return metadataTemplateRow.get(0).findElements(By.className("t-t-remove-btn")).size() > 0;
	}

	public void deleteTemplate(String name) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(4));
		WebElement templateRow = null;
		for (WebElement row : metadataTemplateRow) {
			String text = row.findElement(By.className("t-t-name")).getText();
			if (text.equalsIgnoreCase(name)) {
				templateRow = row;
				break;
			}
		}
		templateRow.findElement(By.className("t-t-remove-button")).click();
		WebElement confirm = wait.until(
				ExpectedConditions.visibilityOfElementLocated(By.className("t-t-confirm-remove")));
		confirm.click();
		wait.until(ExpectedConditions.stalenessOf(confirm));
	}

	public int getNumberOfMetadataTemplates() {
		return metadataTemplateRow.size();
	}

	public boolean canEditTemplateName() {
		try {
			templateEditName.findElement(By.className("ant-typography-edit"));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void editTemplateName(String name) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(4));
		WebElement editIcon = templateEditName.findElement(By.className("ant-typography-edit"));
		editIcon.click();
		wait.until(ExpectedConditions.invisibilityOfAllElements(editIcon));
		WebElement input = driver.switchTo().activeElement();
		input.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
		input.sendKeys(name);
		input.sendKeys(Keys.ENTER);
		wait.until(ExpectedConditions.textToBePresentInElement(templateEditName, name));
	}

	public String getTemplateName() {
		return metadataTemplateName.getText();
	}

	public boolean allFieldsTemplateIsDefault() {
		waitForTemplates();
		// The All Fields template is the last template displayed on the page
		WebElement allFieldsTemplate = metadataTemplateRow.get(metadataTemplateRow.size() - 1);
		List<WebElement> defaultTag = allFieldsTemplate.findElements(By.className("t-t-default-tag"));
		return defaultTag.size() == 1;
	}

	public void setDefaultTemplate(String name) {
		WebElement templateRow = null;
		for (WebElement row : metadataTemplateRow) {
			String text = row.findElement(By.className("t-t-name")).getText();
			if (text.equalsIgnoreCase(name)) {
				templateRow = row;
				break;
			}
		}
		WebElement setDefaultTemplateBtn = templateRow.findElement(By.className("t-t-set-default-button"));
		setDefaultTemplateBtn.click();
	}

	public boolean removeButtonIsDisabled() {
		// First template is set as the default so we get it
		waitForTemplates();
		WebElement currDefaultTemplate = metadataTemplateRow.get(0);
		return !currDefaultTemplate.findElement(By.className("t-t-remove-button")).isEnabled();
	}

	public boolean defaultTemplateTagVisible() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(4));
		WebElement defaultTag = wait.until(
				ExpectedConditions.visibilityOfElementLocated(By.className("t-t-default-tag")));
		return defaultTag.isDisplayed();
	}

	public boolean setDefaultTemplateButtonVisible() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(4));
		WebElement setDefaultTemplateBtn = wait.until(
				ExpectedConditions.visibilityOfElementLocated(By.className("t-t-set-default-button")));
		return setDefaultTemplateBtn.isDisplayed();
	}

	public boolean areFieldRestrictionSettingsVisible() {
		return fieldRestrictions.size() > 0;
	}

	public String getFieldRestrictionForRow(int row) {
		return fieldRestrictions.get(row).findElement(By.className("ant-radio-button-wrapper-checked")).getText();
	}

	public void updateFieldRestrictionToLevel(int row, int optionNumber) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(4));
		WebElement radioButton = fieldRestrictions.get(row)
				.findElements(By.className("ant-radio-button-wrapper"))
				.get(optionNumber);
		radioButton.click();
		wait.until(ExpectedConditions.attributeContains(radioButton, "class", "ant-radio-button-wrapper-checked"));
	}

	private void waitForFields() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(4));
		wait.until(ExpectedConditions.visibilityOfAllElements(metadataFieldRow));
	}

	private void waitForTemplates() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(4));
		wait.until(ExpectedConditions.visibilityOfAllElements(metadataTemplateRow));
	}

	public void removeMetadataField(String field) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(4));
		WebElement button = driver.findElement(
				By.xpath("//td[contains(text(), '" + field + "')]//..//span[contains(@class, 'anticon-close')]"));
		button.click();
		wait.until(ExpectedConditions.stalenessOf(button));
	}

	public int getNumberOfTemplateMetadataFields() {
		return driver.findElements(By.xpath("//tbody/tr['data-row-key']")).size();
	}
}
