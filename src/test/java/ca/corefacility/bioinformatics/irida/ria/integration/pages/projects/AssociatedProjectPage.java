package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class AssociatedProjectPage extends AbstractPage {

	@FindBy(className = "t-selection")
	List<WebElement> selectionSwitches;

	public AssociatedProjectPage(WebDriver driver) {
		super(driver);
	}

	public static AssociatedProjectPage goToPage(WebDriver driver, Long id) {
		get(driver, "projects/" + id + "/settings/associated");
		return PageFactory.initElements(driver, AssociatedProjectPage.class);
	}

	public int getNumberOfTotalProjectsDisplayed() {
		return selectionSwitches.size();
	}

	public int getNumberOfAssociatedProject() {
		return (int) selectionSwitches.stream().filter(elm -> elm.getAttribute("aria-checked").equals("true")).count();
	}

	public void toggleProjectAssociation(int row) {
		WebElement btn = selectionSwitches.get(row);
		String state = btn.getAttribute("aria-checked");
		btn.click();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		wait.until(ExpectedConditions.attributeToBe(btn, "aria-checked", state.equals("true") ? "false" : "true"));
	}
}
