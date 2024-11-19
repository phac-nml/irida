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

/**
 * Page to test the NCBI export feature
 */
public class NcbiExportPage extends AbstractPage {

	@FindBy(className = "t-sample-panel")
	private List<WebElement> samplePanels;

	@FindBy(xpath = "//*[@id=\"bioProject\"]")
	private WebElement bioProjectInput;

	@FindBy(xpath = "//*[@id=\"organization\"]")
	private WebElement organizationInput;

	@FindBy(xpath = "//*[@id=\"namespace\"]")
	private WebElement namespaceInput;

	@FindBy(xpath = "//*[@id=\"releaseDate\"]")
	private WebElement releaseDateInput;

	@FindBy(className = "t-defaults-panel")
	private WebElement defaultsPanel;

	@FindBy(className = "t-default-strategy")
	private WebElement defaultStrategySelect;

	@FindBy(className = "t-submit-button")
	private WebElement submitButton;

	public NcbiExportPage(WebDriver driver) {
		super(driver);
	}

	public static NcbiExportPage init(WebDriver driver) {
		return PageFactory.initElements(driver, NcbiExportPage.class);
	}

	public int getNumberOfSamples() {
		return samplePanels.size();
	}

	public void openSamplePanelBySampleName(String sampleName) {
		for (WebElement panel : samplePanels) {
			String text = panel.findElement(By.className("t-sample-name")).getText();
			if (text.equals(sampleName)) {
				panel.click();
				return;
			}
		}
	}

	public void enterBioProject(String value) {
		bioProjectInput.sendKeys(value);
	}

	public void enterOrganization(String value) {
		organizationInput.sendKeys(value);
	}

	public void enterNamespace(String value) {
		namespaceInput.sendKeys(value);
	}

	public void setReleaseDateInput(String value) {
		releaseDateInput.sendKeys(Keys.chord(Keys.CONTROL, "a"));
		releaseDateInput.sendKeys(value);
		releaseDateInput.sendKeys(Keys.ENTER);
	}

	public void toggleDefaultsPanel() {
		defaultsPanel.click();
	}

	public void setDefaultStrategySelect(String strategy) {
		defaultStrategySelect.click();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));
		List<WebElement> selectOptions = wait.until(
				ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("ant-select-item")));
		for (WebElement option : selectOptions) {
			if (option.getAttribute("title").equals(strategy)) {
				option.click();
				return;
			}
		}
	}

	public void setDefaultInputFieldValue(String field, String value) {
		driver.findElement(By.className("t-default-" + field)).sendKeys(value);
	}

	public String getInputValueForDefaultField(String field) {
		return driver.findElement(By.className("t-default-" + field)).getText();
	}

	public String getSelectValueForSampleField(String field) {
		// NOTE: expect the panel to be open at this point.
		WebElement panel = driver.findElement(By.cssSelector(".t-samples .ant-collapse-content"));
		WebElement input = panel.findElement(By.className("t-sample-" + field));
		return input.findElement(By.className("ant-select-selection-item")).getAttribute("title");
	}

	public String getInputValueForSampleField(String field) {
		return driver.findElement(By.className("t-sample-" + field)).getText();
	}

	public void setTextInputForSampleFieldValue(String field, String value) {
		// NOTE: expect the panel to be open at this point.
		WebElement panel = driver.findElement(By.cssSelector(".t-samples .ant-collapse-content"));
		WebElement input = panel.findElement(By.className("t-sample-" + field));
		input.sendKeys(value);
	}

	public void setSelectForSampleFieldValue(String field, String value) {
		WebElement select = driver.findElement(By.className("t-sample-" + field));
		select.click();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));
		List<WebElement> selectOptions = wait.until(
				ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("ant-select-item")));
		for (WebElement option : selectOptions) {
			if (option.getAttribute("title").equals(value)) {
				option.click();
				return;
			}
		}
	}

	public void setCascaderForSampleField(String field, String firstValue, String secondValue) {
		WebElement cascader = driver.findElement(By.className("t-sample-" + field));
		cascader.click();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));
		List<WebElement> cascaderMenus = wait.until(
				ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("ant-cascader-menu")));
		// /html/body/div[2]/div/div/div/div/ul/li[4]/div[1]
		WebElement firstCascaderMenu = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//ul/li[4]/div[1][contains(text(), '" + firstValue + "')]")));
		firstCascaderMenu.click();
		// menus updated after selections, wait for more.
		// /html/body/div[2]/div/div/div/div/ul[2]/li[4]/div
		WebElement secondCascaderMenu = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//ul[2]/li[4]/div[contains(text(), '" + secondValue + "')]")));
		secondCascaderMenu.click();
	}

	public void removeSample(String sampleName) {
		for (WebElement panel : samplePanels) {
			if (panel.findElement(By.className("t-sample-name")).getText().equals(sampleName)) {
				panel.findElement(By.className("t-remove-btn")).click();
			}
		}
	}

	public boolean isSampleValid(String sampleName) throws Exception {
		for (WebElement panel : samplePanels) {
			if (panel.findElement(By.className("t-sample-name")).getText().equals(sampleName)) {
				return panel.findElement(By.xpath("//span[contains(@class, 'ant-tag')]/span[2]"))
						.getText()
						.equals("VALID");
			}
		}
		throw new Exception("Cannot find sample: " + sampleName);
	}

	public void selectSingleEndSequenceFile(String filename) {
		List<WebElement> labels = driver.findElements(By.className("t-single-name"));
		for (WebElement label : labels) {
			if (label.getText().equals(filename)) {
				label.click();
				return;
			}
		}
	}

	public boolean areFormErrorsPresent() {
		waitForTime(500);
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		try {
			List<WebElement> errors = wait.until(
					ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className("ant-form-item-explain-error")));
			return errors.size() > 0;
		} catch (Exception e) {
			return false;
		}
	}

	public void submitExportForm() {
		submitButton.click();
	}

	public boolean isSuccessAlertDisplayed() {
		return driver.findElements(By.cssSelector(".ant-alert.ant-alert-success")).size() == 1;
	}

	public boolean isUserRedirectedToProjectSamplesPage(Long projectId) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		return wait.until(ExpectedConditions.urlMatches("/projects/" + projectId));
	}
}
