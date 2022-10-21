package ca.corefacility.bioinformatics.irida.ria.integration.pages.admin;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class AdminClientsPage extends AbstractPage {
	public static final String PASSWORD = "password";
	public static final String AUTHORIZATION_CODE = "authorization_code";
	public static final String READ_NO = "no";
	public static final String READ_YES = "read";
	public static final String READ_AUTO = "auto";
	public static final String WRITE_NO = "no";
	public static final String WRITE_YES = "write";
	public static final String WRITE_AUTO = "auto";

	@FindBy(css = ".t-admin-clients-table tbody .ant-table-row")
	private List<WebElement> clientRows;

	@FindBy(className = "t-client-modal")
	private WebElement createModal;

	@FindBy(className = "t-add-client-btn")
	private WebElement addClientButton;

	@FindBy(id = "clientId")
	private WebElement clientIdInput;

	@FindBy(css = "[value=\"password\"]")
	private WebElement passwordRadio;

	@FindBy(css = "[value=\"authorization_code\"]")
	private WebElement authCodeRadio;

	@FindBy(id = "redirectURI")
	private WebElement redirctInput;

	@FindBy(className = "t-read-no")
	private WebElement readNoRadio;

	@FindBy(className = "t-read-allowed")
	private WebElement readYesRadio;

	@FindBy(className = "t-read-auto")
	private WebElement readAutoRadio;

	@FindBy(className = "t-write-no")
	private WebElement writeNoRadio;

	@FindBy(className = "t-write-allowed")
	private WebElement writeYesRadio;

	@FindBy(className = "t-write-auto")
	private WebElement writeAutoRadio;

	@FindBy(className = "t-create-btn")
	private WebElement createBtn;

	public AdminClientsPage(WebDriver driver) {
		super(driver);
	}

	public static AdminClientsPage goTo(WebDriver driver) {
		get(driver, "admin/clients");
		return PageFactory.initElements(driver, AdminClientsPage.class);
	}

	public void createClientWithDetails(String clientId, String grantType, String redirectUrl, String readScope,
			String writeScope) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
		addClientButton.click();
		wait.until(ExpectedConditions.visibilityOf(createModal));

		clientIdInput.sendKeys(clientId);
		if (grantType.equals(PASSWORD)) {
			passwordRadio.click();
		} else if (grantType.equals(AUTHORIZATION_CODE)) {
			authCodeRadio.click();
			redirctInput.sendKeys(redirectUrl);
		}

		// READ SCOPE
		if (readScope.equals(READ_NO)) {
			readNoRadio.click();
		} else if (readScope.equals(READ_YES)) {
			readYesRadio.click();
		} else {
			readAutoRadio.click();
		}

		// WRITE SCOPE
		if (writeScope.equals(WRITE_NO)) {
			writeNoRadio.click();
		} else if (writeScope.equals(WRITE_YES)) {
			writeYesRadio.click();
		} else {
			writeAutoRadio.click();
		}

		createBtn.click();
		waitForTime(300);
	}

	public String getClientSecret(String clientId) {
		for (WebElement row : clientRows) {
			if (row.findElement(By.className("t-client-id")).getText().equals(clientId)) {
				return row.findElement(By.className("t-client-secret")).getText();
			}
		}
		return null;
	}
}
