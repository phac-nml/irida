package ca.corefacility.bioinformatics.irida.ria.integration.pages.clients;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class ClientsPage extends AbstractPage {
	@FindBy(className = "t-client-name")
	private List<WebElement> clientNameLinks;

	public ClientsPage(WebDriver driver) {
		super(driver);
	}

	public static ClientsPage goTo(WebDriver driver) {
		get(driver, "clients");
		return PageFactory.initElements(driver, ClientsPage.class);
	}

	public int clientsTableSize() {
		return clientNameLinks.size();
	}

	public boolean checkClientExistsInTable(String clientId) {
		for (WebElement ele : clientNameLinks) {
			if (ele.getText().equals(clientId)) {
				return true;
			}
		}

		return false;
	}
}
