package ca.corefacility.bioinformatics.irida.ria.integration.pages.user;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.Ajax;

public class UsersPage extends AbstractPage {

	public UsersPage(WebDriver driver) {
		super(driver);
	}

	public void goTo() {
		get(driver, "users");
		waitForAjax();
	}
	
	public int usersTableSize() {
		WebElement element = driver.findElement(By.id("user-table-body"));
		return element.findElements(By.tagName("tr")).size();
	}
	
	public void clickUsernameHeader(){
		WebElement header = driver.findElement(By.id("username-header"));
		header.click();
		waitForAjax();
	}
	
	public List<WebElement> getUsernameColumn() {
		waitForAjax();
		return driver.findElements(By.xpath("//table[@id='projectsTable']/tbody//td[2]"));
	}

	public List<String> getLastLogins() {
		waitForAjax();
		List<WebElement> loginDates = driver.findElements(By.className("last-login"));

		List<String> dates = loginDates.stream().map(d -> d.getText()).collect(Collectors.toList());

		return dates;
	}
	
	private void waitForAjax() {
		Wait<WebDriver> wait = new WebDriverWait(driver, 60);
		wait.until(Ajax.waitForAjax(60000));
	}
}
