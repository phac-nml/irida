package ca.corefacility.bioinformatics.irida.ria.integration.components;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.google.common.collect.Ordering;

/**
 * Generic class to handle all instances of Ant Design Tables.
 */
public class AntTable {
	@FindBy(className = "ant-table")
	WebElement table;

	@FindBy(css = ".ant-table-body .ant-table-fixed .ant-table-row")
	List<WebElement> rows;

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

	public boolean isColumnSorted(String className) {
		List<WebElement> elements = table.findElements(By.className(className));
		/*
		Needed to add the filter to this step because if you are using the Ant Design
		fixed columns on the table it renders multiple copies of the cell, but only one
		has the text.
		 */
		List<String> labels = elements.stream()
				.map(WebElement::getText)
				.filter(i -> i.length() != 0)
				.collect(Collectors.toList());
		return Ordering.natural()
				.isOrdered(labels);
	}

	/**
	 * Do to the complexities of fixes columns there is actually 2 columns rendered.
	 * Column one is the actual column displayed to the user.
	 *
	 * @param className The class name for the column label.
	 */
	public void sortFixedColumn(String className) {
		table.findElements(By.className(className))
				.get(1)
				.click();
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
}
