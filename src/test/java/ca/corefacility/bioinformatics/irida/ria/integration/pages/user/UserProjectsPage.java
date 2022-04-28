package ca.corefacility.bioinformatics.irida.ria.integration.pages.user;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.PageUtilities;

/**
 * User projects page for selenium testing
 */
public class UserProjectsPage extends AbstractPage {

	private PageUtilities pageUtilities;

	public static String PROJECTS_PAGE = "users/1/projects";

	public UserProjectsPage(WebDriver driver) {
		super(driver);
		this.pageUtilities = new PageUtilities(driver);
	}

	public void goTo() {
		get(driver, PROJECTS_PAGE);
	}

	public List<String> getUserProjectIds() {
		List<WebElement> findElements = driver.findElements(By.cssSelector("td.t-projectId"));
		List<String> ids = new ArrayList<>();
		findElements.forEach(ele -> {
			ids.add(ele.getText());
		});
		return ids;
	}

	public void subscribeToFirstProject() {
		List<WebElement> findElements = driver.findElements(By.cssSelector("button.t-emailSubscribed"));
		findElements.get(0).click();
	}

	public boolean checkSuccessNotification() {
		return pageUtilities.checkSuccessNotification();
	}
	
}
