package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page to test the NCBI export feature
 */
public class NcbiExportPage extends AbstractPage {
	private final String SAMPLE_PANELS_CLASS = "t-sample-panel";
	private final String BIOPROJECT_ID = "bioProject";
	private final String ORGANIZATION_ID = "organization";
	private final String NAMESPACE_ID = "namespace";
	private final String DEFAULTS_PANEL_CLASS = "t-defaults-panel";
	private final String DEFAULT_STRATEGY_CLASS = "t-default-strategy";
	private final String SUBMIT_BUTTON_CLASS = "t-submit-button";

	public NcbiExportPage(WebDriver driver) {
		super(driver);
	}

	public int getNumberOfSamples() {
		return driver.findElements(By.className("t-sample-panel")).size();
	}

	public void openSamplePanelBySampleName(String sampleName) {
		List<WebElement> samplePanels = driver.findElements(By.className(SAMPLE_PANELS_CLASS));
		for (WebElement panel : samplePanels) {
			String text = panel.findElement(By.className("t-sample-name")).getText();
			if (text.equals(sampleName)) {
				panel.click();
				return;
			}
		}
	}

	public void enterBioProject(String value) {
		driver.findElement(By.id(BIOPROJECT_ID)).sendKeys(value);
	}

	public void enterOrganization(String value) {
		driver.findElement(By.id(ORGANIZATION_ID)).sendKeys(value);
	}

	public void enterNamespace(String value) {
		driver.findElement(By.id(NAMESPACE_ID)).sendKeys(value);
	}

	public void toggleDefaultsPanel() {
		driver.findElement(By.className(DEFAULTS_PANEL_CLASS)).click();
	}

	public void setDefaultStrategySelect(String strategy) {
		driver.findElement(By.className(DEFAULT_STRATEGY_CLASS)).click();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));
		List<WebElement> selectOptions = wait
				.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("ant-select-item")));
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
		List<WebElement> selectOptions = wait
				.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("ant-select-item")));
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
		List<WebElement> cascaderMenus = wait
				.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("ant-cascader-menu")));
		for (WebElement option : cascaderMenus.get(0).findElements(By.className("ant-cascader-menu-item"))) {
			if (option.getText().equals(firstValue)) {
				option.click();
				break;
			}
		}
		// menus updated after selections, wait for more.
		cascaderMenus = wait.until(ExpectedConditions.numberOfElementsToBe(By.className("ant-cascader-menu"), 2));
		for (WebElement option : cascaderMenus.get(1).findElements(By.className("ant-cascader-menu-item"))) {
			if (option.getText().equals(secondValue)) {
				option.click();
				break;
			}
		}
	}

	public void removeSample(String sampleName) {
		List<WebElement> samplePanels = driver.findElements(By.className(SAMPLE_PANELS_CLASS));
		for (WebElement panel : samplePanels) {
			if (panel.findElement(By.className("t-sample-name")).getText().equals(sampleName)) {
				panel.findElement(By.className("t-remove-btn")).click();
			}
		}
	}

	public boolean isSampleValid(String sampleName) throws Exception {
		List<WebElement> samplePanels = driver.findElements(By.className(SAMPLE_PANELS_CLASS));
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
		waitForTime(250);
		try {
			List<WebElement> errors = driver.findElements(By.className("ant-form-item-explain-error"));
			return errors != null && errors.size() > 0;
		} catch (Exception e) {
			return false;
		}
	}

	public void submitExportForm() {
		driver.findElement(By.className(SUBMIT_BUTTON_CLASS)).click();
	}

	public boolean isSuccessAlertDisplayed() {
		return driver.findElements(By.cssSelector(".ant-alert.ant-alert-success")).size() == 0;
	}
}
