package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ShareSamplesPage {
	@FindBy(className = "t-share-project")
	private WebElement shareProjectSelect;

	public static ShareSamplesPage initPage(WebDriver driver) {
		return PageFactory.initElements(driver, ShareSamplesPage.class);
	}

	public void searchForProject(String name) {
		shareProjectSelect.click();
		WebElement search = shareProjectSelect.findElement(By.className("ant-select-selection-search"));
		search.sendKeys(name);
	}
}
