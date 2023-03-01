package ca.corefacility.bioinformatics.irida.ria.integration.pages.cart;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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

	@FindBy(className = "t-pipeline-card")
	private List<WebElement> pipelineCards;

	public CartPage(WebDriver driver) {
		super(driver);
	}

	public static CartPage goToCart(WebDriver driver) {
		get(driver, "cart/pipelines");
		return initPage(driver);
	}

	public static CartPage initPage(WebDriver driver) {
		waitForTime(500);
		return PageFactory.initElements(driver, CartPage.class);
	}

	public int getNavBarSamplesCount() {
		return Integer.parseInt(driver.findElement(By.className("ant-badge-count")).getAttribute("title"));
	}

	public int getNumberOfSamplesInCart() {
		new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions
				.visibilityOfAllElementsLocatedBy(By.className("t-Assembly_and_Annotation_Pipeline_btn")));
		new WebDriverWait(driver, Duration.ofSeconds(10))
				.until(ExpectedConditions.elementToBeClickable(By.className("t-samples-list")));
		new WebDriverWait(driver, Duration.ofSeconds(10))
				.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className("t-sample-details-btn")));
		return driver.findElements(By.className("t-cart-sample")).size();
	}

	public void selectFirstPipeline() {
		WebElement pipelineCard = pipelineCards.iterator().next();

		pipelineCard.findElement(By.className("t-select-pipeline")).click();
	}

	public boolean onPipelinesView() {
		return pipelinesView.isDisplayed();
	}

	public void removeSampleFromCart(int index) {
		WebElement sample = cartSamples.get(index);
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20L));
		wait.until(ExpectedConditions.elementToBeClickable(By.className("t-remove-sample")));
		sample.findElement(By.className("t-remove-sample")).click();
		waitForTime(500);
	}

	public void removeProjectFromCart() {
		WebElement sample = cartSamples.get(0);
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20L));
		wait.until(ExpectedConditions.invisibilityOfAllElements(driver.findElements(By.className("ant-notification"))));
		wait = new WebDriverWait(driver, Duration.ofSeconds(20L));
		wait.until(ExpectedConditions.elementToBeClickable(By.className("t-remove-project")));

		// Used to bypass tooltip which is intercepting the click during tests.
		WebElement removeProjectButton = sample.findElement(By.className("t-remove-project"));
		JavascriptExecutor js = (JavascriptExecutor) driver; //initialize JavascriptExecutor
		js.executeScript("arguments[0].click();", removeProjectButton); //click the button
		waitForTime(500);
	}

	public void viewSampleDetailsFor(String sampleName) {
		for (WebElement cartSample : cartSamples) {
			final WebElement button = cartSample.findElement(By.className("t-sample-details-btn"));
			if (button.getText().equals(sampleName)) {
				button.click();
				WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10L));
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("t-sample-details-modal")));
				break;
			}
		}
	}

	public void selectPhylogenomicsPipeline() {
		goToPipelinePage("t-SNVPhyl_Phylogenomics_Pipeline_btn");
	}

	public void selectAssemblyPipeline() {
		goToPipelinePage("t-Assembly_and_Annotation_Pipeline_btn");
	}

	public void selectBiohanselPipeline() {
		goToPipelinePage("t-bio_hansel_Pipeline_btn");
	}

	private void goToPipelinePage(String pipeline) {
		get(driver, "cart/pipelines");
		WebElement btn = waitForElementVisible(By.className(pipeline));
		btn.click();
	}
}