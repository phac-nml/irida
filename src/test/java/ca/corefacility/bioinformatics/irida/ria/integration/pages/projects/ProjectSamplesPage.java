package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import ca.corefacility.bioinformatics.irida.ria.integration.utilities.Ajax;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.SortUtilities;
import com.google.common.base.Strings;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * Page Object to represent the project samples page.
 * </p>
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class ProjectSamplesPage {
	private static final String URL = "http://localhost:8080/projects/1/samples";
	public static final String DATE_FORMAT = "dd MMM YYYY";
	private WebDriver driver;

	public ProjectSamplesPage(WebDriver driver) {
		this.driver = driver;
	}

    public void go() {
        driver.get(URL);
        waitForAjax();
    }

	/**
	 * The the h1 heading for the page
	 * 
	 * @return String value from within the h1 tag
	 */
	public String getTitle() {
		return driver.findElement(By.tagName("h1")).getText();
	}

	/**
	 * Get the number of displayed samples on the page.
	 * 
	 * @return integer value of displayed samples on the page.
	 */
	public int getDisplayedSampleCount() {
		return driver.findElements(By.cssSelector("tbody tr")).size();
	}

	/**
	 * Get the number of samples that contain files.
	 * 
	 * @return integer value of samples that contain files.
	 */
	public int getCountOfSamplesWithFiles() {
		return driver.findElements(By.className("glyphicon-chevron-right")).size();
	}

	/**
	 * Get the number of selected inputs in the samples table body.
	 * 
	 * @return integer value of the number of selected inuts contained within
	 *         the tbody
	 */
	public int getSelectedSampleCount() {
		return driver.findElements(By.cssSelector("tbody input[type=\"checkbox\"]:checked")).size();
	}

	/**
	 * Checks to see if the selectAll checkbox is in an indeterminate state.
	 * 
	 * @return True if in an indeterminate state.
	 */
	public boolean isSelectAllInIndeterminateState() {
		String exists = driver.findElement(By.id("selectAll")).getAttribute("indeterminate");
		if (Strings.isNullOrEmpty(exists)) {
			return false;
		}
		return true;
	}

	/**
	 * Checks to see if the selectAll checkbox is in a checked state.
	 * 
	 * @return True if in a checked state.
	 */
	public boolean isSelectAllSelected() {
		return driver.findElement(By.id("selectAll")).getAttribute("checked").equals("true");
	}

	/**
	 * Checks to see if a files detail area is displayed
	 * 
	 * @return True if the file details area is displayed
	 */
	public boolean isFilesAreaDisplayed() {
		return driver.findElements(By.cssSelector("tbody tr.details + tr")).size() == 1;
	}

	/**
	 * Checks to see if the Sample Name column is sorted ascending
	 * 
	 * @return True if entire column is sorted ascending
	 */
	public boolean isSampleNameColumnSortedAsc() {
		List<String> list = driver.findElements(By.cssSelector("tbody td:nth-child(3)")).stream()
				.map(WebElement::getText).collect(Collectors.toList());
		return SortUtilities.isStringListSortedAsc(list);
	}

	/**
	 * Checks to see if the Sample Name column is sorted descending
	 *
	 * @return True if entire column is sorted descending
	 */
	public boolean isSampleNameColumnSortedDesc() {
		List<String> list = driver.findElements(By.cssSelector("tbody td:nth-child(3)")).stream()
				.map(WebElement::getText).collect(Collectors.toList());
		return SortUtilities.isStringListSortedDesc(list);
	}

    /**
	 * Checks to see if the Sample Added On column is sorted ascending
	 *
	 * @return True if entire column is sorted ascending
	 */
	public boolean isAddedOnDateColumnSortedAsc() {
		List<String> list = driver.findElements(By.cssSelector("tbody td:nth-child(5)")).stream()
				.map(WebElement::getText).collect(Collectors.toList());
		return SortUtilities.isDateSortedAsc(list, DATE_FORMAT);
	}

	/**
	 * Checks to see if the Sample Added On column is sorted descending
	 *
	 * @return True if entire column is sorted descending
	 */
	public boolean isAddedOnDateColumnSortedDesc() {
		List<String> list = driver.findElements(By.cssSelector("tbody td:nth-child(5)")).stream()
				.map(WebElement::getText).collect(Collectors.toList());
		return SortUtilities.isDateSortedDesc(list, DATE_FORMAT);
	}

	/**************************************************************************************
	 * EVENTS
	 **************************************************************************************/

	/**
	 * Click on the Select All Checkbox
	 */
	public void clickSelectAllCheckbox() {
		driver.findElement(By.id("selectAll")).click();
	}

	/**
	 * Click on the first checkbox in the tbody.
	 */
	public void clickFirstSampleCheckbox() {
		driver.findElement(By.cssSelector("tbody > tr input[type=\"checkbox\"]")).click();
	}

	/**
	 * Open the row that contains files.
	 */
	public void openFilesView() {
		driver.findElement(By.className("glyphicon-chevron-right")).click();
	}

	/**
	 * Click the table header for the samples name. This will enable sorting by
	 * that column.
	 */
	public void clickSampleNameHeader() {
		driver.findElement(By.cssSelector("thead th:nth-child(3)")).click();
		waitForAjax();
	}

	/**
	 * Click the table header for the samples added on date.. This will enable
	 * sorting by that column.
	 */
	public void clickCreatedDateHeader() {
		driver.findElement(By.cssSelector("thead th:nth-child(5)")).click();
		waitForAjax();
	}

	private void waitForAjax() {
		Wait<WebDriver> wait = new WebDriverWait(driver, 60);
		wait.until(Ajax.waitForAjax(60000));
	}
}
