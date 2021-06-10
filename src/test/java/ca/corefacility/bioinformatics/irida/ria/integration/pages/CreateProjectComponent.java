package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

/**
 * <p>
 * Page Object to represent the projects-new page used to create a new project.
 * </p>
 *
 */
public class CreateProjectComponent extends AbstractPage {

	public CreateProjectComponent(WebDriver driver) {
		super(driver);
	}

	public CreateProjectComponent init(WebDriver driver) {
		return PageFactory.initElements(driver, CreateProjectComponent.class);
	}
}
