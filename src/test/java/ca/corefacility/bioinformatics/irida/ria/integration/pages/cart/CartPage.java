package ca.corefacility.bioinformatics.irida.ria.integration.pages.cart;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class CartPage extends AbstractPage {
	@FindBy(className = "t-cart-empty")
	private WebElement emptyCartElement;

	@FindBy(className = "t-cart-sample")
	private List<WebElement> cartSamples;

	@FindBy(className = "t-pipelines")
	private WebElement pipelinesView;

	public CartPage(WebDriver driver) {
		super(driver);
	}

	public static CartPage goToCart(WebDriver driver) {
		get(driver, "/cart/pipelines");
		waitForTime(500);
		return PageFactory.initElements(driver, CartPage.class);
	}

	public int getNavBarSamplesCount() {
		return Integer.parseInt(driver.findElement(By.className("t-cart-count"))
				.getText());
	}

	public int getNumberOfSamplesInCart() {
		new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
				By.className("t-Assembly_and_Annotation_Pipeline_btn")));
		new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(By.className("t-samples-list")));
		new WebDriverWait(driver, 10).until(
				ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className("t-sample-name")));
		return driver.findElements(By.className("t-cart-sample"))
				.size();
	}

	public boolean onPipelinesView() {
		return pipelinesView.isDisplayed();
	}

	public void removeSampleFromCart(int index) {
		WebElement sample = cartSamples.get(index);
		WebElement deleteButton = sample.findElement(By.className("t-delete-menu-btn"));
		Actions actions = new Actions(driver);
		actions.moveToElement(deleteButton).perform();
		WebElement deleteMenu = driver.findElement(By.className("t-delete-menu"));
		deleteMenu.findElement(By.className("t-delete-sample"))
				.click();
		waitForTime(500);
	}

	public void removeProjectFromCart() {
		WebElement sample = cartSamples.get(0);
		sample.findElement(By.className("t-delete-menu-btn"))
				.click();
		WebElement deleteMenu = driver.findElement(By.className("t-delete-menu"));
		deleteMenu.findElement(By.className("t-delete-project"))
				.click();
		waitForTime(500);
	}

	public void selectPhylogenomicsPipeline() {
		goToPipelinePage("t-SNVPhyl_Phylogenomics_Pipeline_btn");
	}

	public void selectAssemblyPipeline() {
		goToPipelinePage("t-Assembly_and_Annotation_Pipeline_btn");
	}

	private void goToPipelinePage(String pipeline) {
		get(driver, "cart/pipelines");
		WebElement btn = waitForElementVisible(By.className(pipeline));
		btn.click();
	}
}
