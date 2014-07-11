package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * User details page for selenium testing
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public class UserDetailsPage {
	public static String EDIT_USER_LINK = "editUser";
	public static String USER_ID = "user-id";
	private WebDriver driver;

	public UserDetailsPage(WebDriver driver) {
		this.driver = driver;
		driver.get("http://localhost:8080/users/1");
	}

	public String getCurrentUserId() {
		driver.get("http://localhost:8080/users/current");
		WebElement findElement = driver.findElement(By.id(USER_ID));
		return findElement.getText();
	}

	public String getOtherUserId(Long id) {
		driver.get("http://localhost:8080/users/" + id);
		WebElement findElement = driver.findElement(By.id(USER_ID));
		return findElement.getText();
	}

	public boolean canGetEditLink(Long id) {
		driver.get("http://localhost:8080/users/" + id);
		try {
			driver.findElement(By.id(EDIT_USER_LINK));
			return true;
		} catch (NoSuchElementException ex) {
			return false;
		}
	}

	public List<String> getUserProjectIds(Long id) {
		driver.get("http://localhost:8080/users/" + id);
		List<WebElement> findElements = driver.findElements(By.className("user-project-id"));
		List<String> ids = new ArrayList<>();
		findElements.forEach(ele -> {
			ids.add(ele.getText());
		});
		return ids;
	}
}
