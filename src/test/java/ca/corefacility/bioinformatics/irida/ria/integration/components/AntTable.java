package ca.corefacility.bioinformatics.irida.ria.integration.components;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Ordering;

/**
 * Generic class to handle all instances of Ant Design Tables.
 */
public class AntTable {
	//Dec 31, 1969, 6:00:00 PM || Apr 1, 2020, 5:23:41 AM || Dec 31, 1969, 6:00:00 PM
	public static String LONG_DATE_FORMAT = "MMM d, yyyy, h:mm:ss aaa";

	@FindBy(css = ".ant-table-content table")
	WebElement table;

	@FindBy(className = "ant-table-row")
	List<WebElement> rows;

	@FindBy(css = ".ant-pagination-next button")
	WebElement nextButton;

	@FindBy(css = ".ant-pagination-prev button")
	WebElement prevButton;

	@FindBy(css = ".t-search input")
	WebElement searchInput;

	public static AntTable getTable(WebDriver driver) {
		return PageFactory.initElements(driver, AntTable.class);
	}

	/**
	 * Get the number of rows currently displayed in the table.
	 * NOTE: This does not take into account paged elements or scroll
	 * element that are hidden from view.
	 *
	 * @return number of rows
	 */
	public List<WebElement> getRows() {
		return rows;
	}

	public boolean isColumnSorted(String className, String dateFormat) {
		List<WebElement> elements = table.findElements(By.className(className));

		/*
		Needed to add the filter to this step because if you are using the Ant Design
		fixed columns on the table it renders multiple copies of the cell, but only one
		has the text.
		 */
		List<String> labels = elements.stream()
				.map(WebElement::getText)
				.filter(i -> !Strings.isNullOrEmpty(i))
				.collect(Collectors.toList());
		if (Strings.isNullOrEmpty(dateFormat)) {
			return Ordering.natural()
					.isOrdered(labels);
		} else {
			DateFormat sdf = new SimpleDateFormat(dateFormat);
			sdf.setLenient(false);
			List<Long> dates = labels.stream()
					.map(s -> getMillisecondsFromDate(s, sdf))
					.filter(Objects::nonNull)
					.collect(Collectors.toList());
			return Ordering.natural()
					.isOrdered(dates);
		}
	}

	/**
	 * Sort any non-fixed columns.
	 *
	 * @param className The class name for the column label.
	 */
	public void sortColumn(String className) {
		table.findElement(By.className(className))
				.click();
	}

	private Long getMillisecondsFromDate(String dateStr, DateFormat dateFormat) {
		try {
			return (dateFormat.parse(dateStr)).getTime();
		} catch (ParseException e) {
			return null;
		}
	}

	public void goToNextPage() {
		nextButton.click();
	}

	public void goToPrevPage() {
		prevButton.click();
	}

	public void search(String text) {
		if (!Strings.isNullOrEmpty(text)) {
			searchInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), text, Keys.ENTER);
		} else {
			searchInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE, Keys.ENTER);
		}
	}
}
