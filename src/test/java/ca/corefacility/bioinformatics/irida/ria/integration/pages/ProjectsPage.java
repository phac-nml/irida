package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.utilities.Ajax;

import com.google.common.base.Strings;

/**
 * <p>
 * Page Object to represent the projects page.
 * </p>
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class ProjectsPage extends AbstractPage {
    private static final Logger logger = LoggerFactory.getLogger(ProjectsPage.class);
	public static final String RELATIVE_URL = "projects";
	public static final String ADMIN_URL = RELATIVE_URL + "/all";

    public ProjectsPage(WebDriver driver) {
		super(driver);
	}

	public void toUserProjectsPage() {
		get(driver, RELATIVE_URL);
	}

	public void toAdminProjectsPage() {
		get(driver, ADMIN_URL);
	}

	public int projectsTableSize() {
		logger.trace("Getting table size");
		return driver.findElements(By.cssSelector("#projectsTable tbody tr")).size();
	}

	public String getCollaboratorClass() {
		return driver.findElement(
				By.cssSelector("#projectsTable tbody tr:nth-child(3) td:nth-child(4) span")).getAttribute("class");
	}

	public String getOwnerClass() {
		return driver.findElement(By.cssSelector("#projectsTable tbody tr:nth-child(1) td:nth-child(4) span")).getAttribute(
				"class");
	}

	public List<WebElement> getProjectColumn() {
		waitForAjax();
		return driver.findElements(By.cssSelector("#projectsTable tbody td:nth-child(2)"));
	}

	public void clickProjectNameHeader(){
		driver.findElement(By.id("project-name")).click();
		waitForAjax();
	}

    public boolean adminShouldBeAbleToSelectViaCheckboxes() {
        return driver.findElements(By.cssSelector("#projectsTable input[type=\"checkbox\"]")).size() > 0;
    }

    public int adminGetSelectedCheckboxCount() {
        return driver.findElements(By.cssSelector("#projectsTable tbody input[type=\"checkbox\"]:checked")).size();
    }

    public void adminSelectHeaderCheckbox() {
        driver.findElement(By.id("selectAll")).click();
    }

    public void adminSelectFirstCheckbox() {
        List<WebElement> els = driver.findElements(By.cssSelector("#projectsTable tbody input[type=\"checkbox\"]"));
        els.get(0).click();
    }

    public boolean adminIsSelectAllCheckboxIntermediateState() {
        String exists = driver.findElement(By.id("selectAll")).getAttribute("indeterminate");
        if (Strings.isNullOrEmpty(exists)) {
            return false;
        }
        return true;
    }

	private void waitForAjax() {
		Wait<WebDriver> wait = new WebDriverWait(driver, 60);
		wait.until(Ajax.waitForAjax(60000));
	}
}
