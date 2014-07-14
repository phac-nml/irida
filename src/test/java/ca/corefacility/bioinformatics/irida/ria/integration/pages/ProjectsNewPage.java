package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

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

	private WebElement name;
	private WebElement organism;
	private WebElement wiki;
	private WebElement description;
	private WebElement submitBtn;

	public ProjectsNewPage(WebDriver driver) {
		this.driver = driver;
		driver.get(PROJECT_NEW_URL);
		this.name = driver.findElement(By.name("name"));
		this.organism = driver.findElement(By.name("organism"));
		this.wiki = driver.findElement(By.name("remoteURL"));
		this.description = driver.findElement(By.name("projectDescription"));
		this.submitBtn = driver.findElement(By.className("btn-primary"));
	}

	public void submitForm(String name, String organism, String wiki, String description) {
		this.name.sendKeys(name);
		this.organism.sendKeys(organism);
		this.wiki.sendKeys(wiki);
		this.description.sendKeys(description);
		this.submitBtn.click();
	}

	public void setName(String name) {
        this.wiki.clear();
		this.name.sendKeys(name);
		this.organism.click();
	}

	public List<String> getErrors() {
        List<WebElement> elements = driver.findElements(By.cssSelector("section#errors-default a"));
        return elements.stream().map(WebElement::getText).collect(Collectors.toList());
    }

	public boolean checkForErrors() {
		return driver.findElements(By.id("errors-default")).size() == 0;
	}

	public void setURL(String url) {
        this.wiki.clear();
		this.wiki.sendKeys(url);
		this.description.click();
	}

    public void submit() {
        this.submitBtn.click();
    }
}
