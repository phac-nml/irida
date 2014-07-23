package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * Page Object to represent the projects-new page used to create a new project.
 * </p>
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class ProjectsNewPage {
	public static final String PROJECT_NEW_URL = "http://localhost:8080/projects/new";
	private WebDriver driver;

	public ProjectsNewPage(WebDriver driver) {
		this.driver = driver;
	}

    public void goToPage(){
        driver.get(PROJECT_NEW_URL);
    }

	public void submitForm(String name, String organism, String wiki, String description) {
        driver.findElement(By.name("name")).sendKeys(name);
        WebElement organismField = driver.findElement(By.cssSelector("a.select2-choice"));
        organismField.click();
        WebElement sdf = driver.findElement(By.name("organism"));
        sdf.sendKeys(organism);
        sdf.sendKeys(Keys.RETURN);
        driver.findElement(By.name("remoteURL")).sendKeys(wiki);
        driver.findElement(By.name("projectDescription")).sendKeys(description);
        driver.findElement(By.className("btn-primary")).click();
	}

	public void setName(String name) {
        WebElement nameField = driver.findElement(By.name("name"));
        nameField.sendKeys(name);
        nameField.sendKeys(Keys.TAB);
	}

	public List<String> getErrors() {
        List<WebElement> elements = driver.findElements(By.cssSelector("section#errors-default a"));
        return elements.stream().map(WebElement::getText).collect(Collectors.toList());
    }

	public boolean formHasErrors() {
        boolean result;
        try {
            List<WebElement> elements = driver.findElements(By.id("errors-default"));
            result = elements.size() == 0 ? false : true;
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

	public void setRemoteURL(String url) {
        WebElement urlField = driver.findElement(By.name("remoteURL"));
        urlField.clear();
        urlField.sendKeys(url);
        urlField.sendKeys(Keys.TAB);
	}
}
