package ca.corefacility.bioinformatics.irida.ria.integration.pages.clients;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class ClientsPage extends AbstractPage {

	public ClientsPage(WebDriver driver) {
		super(driver);
	}

	public void goTo(){
		get(driver , "clients");
		waitForTime(400);
	}

	public int clientsTableSize() {
		return driver.findElements(By.cssSelector(".ant-table-body .ant-table-row")).size();
	}

	public boolean checkClientExistsInTable(String clientId) {
		List<WebElement> findElements = driver.findElements(By.className("t-client-name"));
		for (WebElement ele : findElements) {
			if (ele.getText().equals(clientId)) {
				return true;
			}
		}

		return false;
	}
}
