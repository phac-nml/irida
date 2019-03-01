package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CartPage extends AbstractPage {

	public CartPage(WebDriver driver) {
		super(driver);
	}

	public void selectPhylogenomicsPipeline() {
		goToPipelinePage("t-SNVPhyl_Phylogenomics_Pipeline_btn");
	}

	public void selectAssymblyPipeline() {
		goToPipelinePage("t-Assembly_and_Annotation_Pipeline_btn");
	}

	private void goToPipelinePage(String pipeline) {
		get(driver, "cart/pipelines");
		WebElement btn = waitForElementVisible(By.className(pipeline));
		btn.click();
	}
}
