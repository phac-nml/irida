package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * Page representing the project samples line list.
 */
public class ProjectLineListPage extends AbstractPage {
	@FindBy(css = "#linelist th")
	List<WebElement> linelistHeaders;

	public ProjectLineListPage(WebDriver driver) {
		super(driver);
	}

	public static ProjectLineListPage goToPage(WebDriver driver) {
		get(driver, "/projects/1/linelist");
		ProjectLineListPage page = PageFactory.initElements(driver, ProjectLineListPage.class);
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector("#linelist tbody"))));
		return page;
	}

	public int getTableHeaderCount() {
		return linelistHeaders.size();
	}
}
