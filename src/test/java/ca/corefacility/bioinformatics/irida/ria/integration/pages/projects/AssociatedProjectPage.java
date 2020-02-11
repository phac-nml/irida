package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class AssociatedProjectPage extends AbstractPage {
	private static final Logger logger = LoggerFactory.getLogger(AssociatedProjectPage.class);

	@FindBy(className = "t-selection")
	List<WebElement> selectionSwitches;

	public AssociatedProjectPage(WebDriver driver) {
		super(driver);
	}

	public static AssociatedProjectPage goToPage(WebDriver driver, Long id) {
		get(driver, "/projects/" + id + "/settings/associated");
		return PageFactory.initElements(driver, AssociatedProjectPage.class);
	}

	public int getNumberOfTotalProjectsDisplayed() {
		return selectionSwitches.size();
	}

	public int getNumberOfAssociatedProject() {
		return (int) selectionSwitches.stream()
				.filter(elm -> elm.getAttribute("aria-checked")
						.equals("true"))
				.count();
	}

	public void toggleProjectAssociation(int row) {
		WebElement btn = selectionSwitches.get(row);
		String value = btn.getAttribute("aria-checked");
		btn.click();
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.attributeContains(btn, "aria-checked", value.equals("true") ? "false" : "true"));
	}
}
