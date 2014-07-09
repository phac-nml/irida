package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import ca.corefacility.bioinformatics.irida.ria.integration.utilities.Ajax;

public class UsersPage {
	private WebDriver driver;

	public UsersPage(WebDriver driver) {
		this.driver = driver;
		driver.get("http://localhost:8080/users");
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
	
	private void waitForAjax() {
		Wait<WebDriver> wait = new WebDriverWait(driver, 60);
		wait.until(Ajax.waitForAjax(60000));
	}
}
