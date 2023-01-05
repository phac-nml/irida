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
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class ProjectSyncPage extends AbstractPage {

	@FindBy(className = "t-api-select")
	private WebElement apiSelection;

	@FindBy(className = "t-project-select")
	private WebElement projectSelection;

	@FindBy(className = "t-project-url")
	private WebElement projectUrlTextBox;

	@FindBy(className = "t-sync-submit")
	private WebElement submitBtn;

	public ProjectSyncPage(WebDriver driver) {
		super(driver);
	}

	public static ProjectSyncPage goTo(WebDriver driver) {
		String url = "/projects/synchronize";
		get(driver, url);
		return PageFactory.initElements(driver, ProjectSyncPage.class);
	}

	public void selectApi(int index) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
		wait.until(ExpectedConditions.elementToBeClickable(apiSelection));
		apiSelection.findElement(By.className("ant-select-selection-item")).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ant-select-item")));
		driver.findElements(By.className("ant-select-item")).get(index).click();
	}

	public boolean areProjectsAvailable() {
		return projectSelection.isDisplayed();
	}

	public void selectProjectInListing(String name) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
		wait.until(ExpectedConditions.elementToBeClickable(projectSelection));
		projectSelection.findElement(By.className("ant-select-selection-search")).click();
		driver.switchTo().activeElement().sendKeys(name);
		driver.switchTo().activeElement().sendKeys(Keys.ENTER);
	}

	public String getSelectedProjectName() {
		return new Select(projectSelection).getFirstSelectedOption().getText();
	}

	public void setProjectUrl(String url) {
		projectUrlTextBox.clear();
		projectUrlTextBox.sendKeys(url);
	}

	public String getProjectUrl() {
		return projectUrlTextBox.getAttribute("value");
	}

	public void submitProject() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		wait.until(ExpectedConditions.elementToBeClickable(submitBtn));
		submitBtn.click();
		waitForTime(500);
	}

	public boolean isResourceNotFoundErrorMessageDisplayed() {
		List<WebElement> elements = driver.findElements(By.className("ant-form-item-explain-error"));

		if(elements.size() == 1) {
			return elements.get(0).getText().contains("Resource not found");
		}
		return false;
	}

	public boolean isAccessDeniedErrorMessageDisplayed() {
		List<WebElement> elements = driver.findElements(By.className("ant-form-item-explain-error"));

		if(elements.size() == 1) {
			return elements.get(0).getText().contains("Access denied");
		}
		return false;
	}

	public void clickSetUrlManuallyCheckbox() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		WebElement element = driver.findElement(By.className("t-remote-project-url-checkbox"));
		wait.until(ExpectedConditions.elementToBeClickable(element));
		element.click();
		waitForTime(500);
	}

	public void setRemoteProjectUrl(String projectUrl) {
		WebElement element = driver.findElement(By.className("t-project-url"));
		element.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
		element.sendKeys(projectUrl);
		waitForTime(500);
	}

}
