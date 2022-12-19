package ca.corefacility.bioinformatics.irida.ria.integration.components;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class AddMemberButton {
	@FindBy(className = "t-add-member-btn")
	private WebElement addMemberBtn;

	@FindBy(className = "t-add-member-modal")
	private WebElement addMemberModal;

	@FindBy(className = "t-new-member")
	private List<WebElement> newMemberList;

	@FindBy(css = ".t-add-member-modal .ant-select-selection-search-input")
	private WebElement addMemberInput;

	private static WebDriverWait wait;

	public static AddMemberButton getAddMemberButton(WebDriver driver) {
		wait = new WebDriverWait(driver, Duration.ofSeconds(3L));
		return PageFactory.initElements(driver, AddMemberButton.class);
	}

	public void addMember(WebDriver driver, String name, String role) {
		wait.until(ExpectedConditions.elementToBeClickable(addMemberBtn));
		addMemberBtn.click();
		wait.until(ExpectedConditions.visibilityOf(addMemberModal));
		AbstractPage.waitForTime(100);
		WebElement input = driver.switchTo().activeElement();
		input.sendKeys(name);
		wait.until(ExpectedConditions.visibilityOf(newMemberList.get(0)));
		newMemberList.get(0).click();
		if (role.equals("PROJECT_OWNER")) {
			WebElement element = driver.findElements(By.className("t-project-role-owner")).get(0);
			wait.until(ExpectedConditions.elementToBeClickable(element));
			element.click();
		} else if (role.equals("PROJECT_USER")) {
			WebElement element = driver.findElements(By.className("t-project-role-collaborator")).get(0);
			wait.until(ExpectedConditions.elementToBeClickable(element));
			element.click();
		} else if (role.equals("GROUP_OWNER")) {
			WebElement element = driver.findElements(By.className("t-group-role-owner")).get(0);
			wait.until(ExpectedConditions.elementToBeClickable(element));
			element.click();
		} else if (role.equals("GROUP_MEMBER")) {
			WebElement element = driver.findElements(By.className("t-group-role-member")).get(0);
			wait.until(ExpectedConditions.elementToBeClickable(element));
			element.click();
		}
		WebElement modalOkBtn = addMemberModal.findElement(By.cssSelector(".ant-btn.ant-btn-primary"));
		wait.until(ExpectedConditions.elementToBeClickable(modalOkBtn));
		modalOkBtn.click();
		wait.until(ExpectedConditions.invisibilityOf(addMemberModal));
	}
}
