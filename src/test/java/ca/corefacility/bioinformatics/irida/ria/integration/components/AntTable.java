package ca.corefacility.bioinformatics.irida.ria.integration.components;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.google.common.collect.Ordering;

/**
 * Generic class to handle all instances of Ant Design Tables.
 */
public class AntTable {

	@FindBy(css = ".ant-input-search .ant-input")
	WebElement inputSearch;

	@FindBy(className = "ant-table")
	WebElement table;

	@FindBy(css = ".ant-table-body .ant-table-fixed .ant-table-row")
	List<WebElement> fixedTableRows;

	@FindBy(css = ".ant-table-content .ant-table-tbody .ant-table-row")
	List<WebElement> nonFixedTableRows;

	public static AntTable getTable(WebDriver driver) {
		return PageFactory.initElements(driver, AntTable.class);
	}

	/**
	 * Get the number of rows currently displayed in the table.
	 * NOTE: This does not take into account paged elements or scroll
	 * element that are hidden from view.
	 * 
	 * Since fixed column table and regular tables have a slightly different layout,
	 * we first check to see if the fixed table has any row if it doesn't then we
	 * can check a regular table.  This works because they both initialize to 0.
	 *
	 * @return number of rows
	 */
	public List<WebElement> getRows() {
		return fixedTableRows.size() > 0 ? fixedTableRows : nonFixedTableRows;
	}

	/**
	 * Determine if a column is sorted Ascending.
	 *
	 * @param className - The className associated with the cell.
	 * @return true if it is sorted
	 */
	public boolean isColumnSortedAscending(String className) {
		List<String> labels = getCellValuesByColumnClassName(className);
		return Ordering.natural()
				.isOrdered(labels);
	}

	/**
	 * Determine if a column is sorted Descending.
	 *
	 * @param className - The className associated with the cell.
	 * @return true if it is sorted
	 */
	public boolean isColumnSortDescending(String className) {
		List<String> labels = getCellValuesByColumnClassName(className);
		return Ordering.natural()
				.reverse()
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

	public List<String> getCellValuesByColumnClassName(String className) {
		List<WebElement> elements = getCellsByClassName(className);
		return getCellValues(elements);
	}

	public void searchTable(String term) {
		inputSearch.sendKeys(term);
		inputSearch.sendKeys(Keys.ENTER);
	}

	private List<WebElement> getCellsByClassName(String className) {
		return table.findElements(By.className(className));
	}

	private List<String> getCellValues(List<WebElement> elements) {
	/*
	Needed to add the filter to this step because if you are using the Ant Design
	fixed columns on the table it renders multiple copies of the cell, but only one
	has the text.
	 */
		return elements.stream()
				.map(WebElement::getText)
				.filter(i -> i.length() != 0)
				.collect(Collectors.toList());
	}
}
