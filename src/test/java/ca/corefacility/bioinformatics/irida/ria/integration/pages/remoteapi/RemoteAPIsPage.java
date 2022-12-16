package ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class RemoteAPIsPage extends AbstractPage {
	private static final String RELATIVE_URL = "remote_api";

	@FindBy(className = "t-remoteapi-table")
	private WebElement table;

	@FindBy(className = "t-add-remote-api-btn")
	private WebElement addRemoteButton;

	@FindBy(className = "t-create-api")
	private WebElement addRemoteModal;

	@FindBy(id = "remote_api_name")
	private WebElement nameInput;

	@FindBy(id = "remote_api_clientId")
	private WebElement clientIdInput;

	@FindBy(id = "remote_api_clientSecret")
	private WebElement clientSecretInput;

	@FindBy(id = "remote_api_serviceURI")
	private WebElement serviceURIInput;

	@FindBy(className = "t-submit-btn")
	private WebElement submitCreateRemoteBtn;

	@FindBy(css = ".ant-form-item-explain div[role=\"alert\"]")
	private List<WebElement> errorAlerts;

	public RemoteAPIsPage(WebDriver driver) {
		super(driver);
		get(driver, RELATIVE_URL);
	}

	public static RemoteAPIsPage goTo(WebDriver driver) {
		get(driver, "admin/" + RELATIVE_URL);
		return PageFactory.initElements(driver, RemoteAPIsPage.class);
	}

	public static RemoteAPIsPage goToUserPage(WebDriver driver) {
		get(driver, RELATIVE_URL);
		return PageFactory.initElements(driver, RemoteAPIsPage.class);
	}

	public int remoteApisTableSize() {
		return table.findElements(By.className("ant-table-row")).size();
	}

	public boolean checkRemoteApiExistsInTable(String clientName) {
		List<WebElement> findElements = table.findElements(By.className("t-api-name"));
		for (WebElement ele : findElements) {
			if (ele.getText().equals(clientName)) {
				return true;
			}
		}

		return false;
	}

	public boolean canSeeConnectButton() {
		return table.findElements(By.className("t-remote-status-connect")).size() > 0;
	}

	public void openAddRemoteModal() {
		addRemoteButton.click();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
		wait.until(ExpectedConditions.visibilityOf(addRemoteModal));
	}

	public void enterApiDetails(String name, String clientId, String clientSecret, String serviceURI) {
		nameInput.sendKeys(name);
		clientIdInput.sendKeys(clientId);
		clientSecretInput.sendKeys(clientSecret);
		serviceURIInput.sendKeys(serviceURI);
	}

	public void submitCreateFormWithErrors() {
		submitCreateRemoteBtn.click();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.cssSelector(".ant-form-item-explain.ant-form-item-explain-error")));
	}

	public void submitCreateForm() {
		submitCreateRemoteBtn.click();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
		wait.until(ExpectedConditions.invisibilityOf(addRemoteModal));
	}

	public List<String> getCreateErrors() {
		return errorAlerts.stream().map(WebElement::getText).collect(Collectors.toUnmodifiableList());
	}

	public void createRemoteApi(String name, String clientId, String clientSecret, String serviceURI) {
		openAddRemoteModal();
		enterApiDetails(name, clientId, clientSecret, serviceURI);
		submitCreateForm();
	}

	public void connectToRemoteAPI(String clientName) {
		List<WebElement> rows = table.findElements(By.className("ant-table-row"));
		for (WebElement row : rows) {
			WebElement nameCell = row.findElement(By.className("t-api-name"));
			if (nameCell != null && nameCell.getText().equals(clientName)) {
				WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
				WebElement connectBtn = row.findElement(By.className("t-remote-status-connect"));
				wait.until(ExpectedConditions.elementToBeClickable(connectBtn));
				connectBtn.click();
				waitForTime(400);
				clickAuthorize();
			}
		}

	}

	private void clickAuthorize() {
		String parentWindowHandler = driver.getWindowHandle(); // Store your parent window
		String subWindowHandler = null;

		Set<String> handles = driver.getWindowHandles(); // get all window handles
		for (String handle : handles) {
			subWindowHandler = handle;
		}
		driver.switchTo().window(subWindowHandler); // switch to popup window

		// Now you are in the popup window, perform necessary actions here

		WebElement authorizeButton = driver.findElement(By.id("authorize-btn"));
		authorizeButton.click();

		driver.switchTo().window(parentWindowHandler); // switch back to parent window

		waitForTime(8000);
	}
}
