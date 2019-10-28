package ca.corefacility.bioinformatics.irida.ria.integration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Select2Utility {
	private WebDriver driver;

	public Select2Utility(WebDriver driver) {
		this.driver = driver;
	}

	public void openSelect2Input() {
		WebElement opener = driver.findElement(By.cssSelector("a.select2-choice"));
		opener.click();
	}

	public void searchByText(String text) {
		WebDriverWait wait = new WebDriverWait(driver, 10L);
		WebElement input = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a.select2-choice")));
		input.sendKeys(text);
	}

	public void selectDefaultMatch() {
		WebDriverWait wait = new WebDriverWait(driver, 10L);
		WebElement match = wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.className("select2-match"))));
		match.click();
	}
}
