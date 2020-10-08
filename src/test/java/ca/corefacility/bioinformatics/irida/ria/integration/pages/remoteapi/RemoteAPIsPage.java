package ca.corefacility.bioinformatics.irida.ria.integration.pages.remoteapi;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class RemoteAPIsPage extends AbstractPage {
	private static final String RELATIVE_URL = "remote_api";

	@FindBy(css = ".t-remoteapi-table table")
	private WebElement table;

	public RemoteAPIsPage(WebDriver driver) {
		super(driver);
		get(driver, RELATIVE_URL);
	}

	public static RemoteAPIsPage goTo(WebDriver driver) {
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
}
