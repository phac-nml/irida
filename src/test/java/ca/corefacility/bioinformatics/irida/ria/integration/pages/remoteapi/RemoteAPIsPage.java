package ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

import com.google.common.base.Strings;

public class RemoteAPIsPage extends AbstractPage {
	private static final String RELATIVE_URL = "admin/remote_api";

	@FindBy(css = ".t-remoteapi-table table")
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
		get(driver, RELATIVE_URL);
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
			if (ele.getText()
					.equals(clientName)) {
				return true;
			}
		}

		return false;
	}

	public boolean canSeeRemoteDetails() {
		return !Strings.isNullOrEmpty(table.findElement(By.className("t-api-name"))
				.getAttribute("href"));
	}

	public void openAddRemoteModal() {
		addRemoteButton.click();
		WebDriverWait wait = new WebDriverWait(driver, 2);
		wait.until(ExpectedConditions.visibilityOf(addRemoteModal));
	}

	public void enterApiDetails(String name, String clientId, String clientSecret, String serviceURI) {
		nameInput.sendKeys(Keys.chord(Keys.CONTROL, "a"));
		nameInput.sendKeys(name);
		clientIdInput.sendKeys(Keys.chord(Keys.CONTROL, "a"));
		clientIdInput.sendKeys(clientId);
		clientSecretInput.sendKeys(Keys.chord(Keys.CONTROL, "a"));
		clientSecretInput.sendKeys(clientSecret);
		serviceURIInput.sendKeys(Keys.chord(Keys.CONTROL, "a"));
		serviceURIInput.sendKeys(serviceURI);
	}

	public void submitCreateFormWithErrors() {
		submitCreateRemoteBtn.click();
		WebDriverWait wait = new WebDriverWait(driver, 2);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".ant-form-item-explain.ant-form-item-explain-error")));
	}

	public void submitCreateForm() {
		submitCreateRemoteBtn.click();
		WebDriverWait wait = new WebDriverWait(driver, 2);
		wait.until(ExpectedConditions.invisibilityOf(addRemoteModal));
	}

	public List<String> getCreateErrors() {
		return errorAlerts.stream()
				.map(WebElement::getText)
				.collect(Collectors.toUnmodifiableList());
	}

	public void createRemoteApi(String name, String clientId, String clientSecret, String serviceURI) {
		openAddRemoteModal();
		enterApiDetails(name, clientId, clientSecret, serviceURI);
		submitCreateForm();
	}
}
