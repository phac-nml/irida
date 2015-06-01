package ca.corefacility.bioinformatics.irida.ria.integration.users;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITPhantomJS;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.user.UsersPage;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * <p> Integration test to ensure that the Projects Page. </p>
 *
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/users/UsersPageIT.xml")
public class UsersPageIT extends AbstractIridaUIITPhantomJS {
	private UsersPage usersPage;

	@Before
	public void setUpTest() {
		LoginPage.loginAsManager(driver());
		usersPage = new UsersPage(driver());
	}

	@Test
	public void confirmTablePopulatedByProjects() {
		usersPage.goTo();
		assertEquals("Projects table should be populated by 3 projects", 3, usersPage.usersTableSize());
	}

	@Test
	public void sortByUserName() {
		usersPage.goTo();
		usersPage.clickUsernameHeader();
		List<WebElement> ascElements = usersPage.getUsernameColumn();
		assertTrue("Projects page is sorted Ascending", checkSortedAscending(ascElements));

		usersPage.clickUsernameHeader();
		List<WebElement> desElements = usersPage.getUsernameColumn();
		assertTrue("Projects page is sorted Descending", checkSortedDescending(desElements));
	}

	/**
	 * Checks if a List of {@link WebElement} is sorted in ascending order.
	 *
	 * @param elements
	 * 		List of {@link WebElement}
	 *
	 * @return if the list is sorted ascending
	 */

	private boolean checkSortedAscending(List<WebElement> elements) {
		boolean isSorted = true;
		for (int i = 1; i < elements.size(); i++) {
			if (elements.get(i).getText().compareTo(elements.get(i - 1).getText()) < 0) {
				isSorted = false;
				break;
			}
		}
		return isSorted;
	}

	/**
	 * Checks if a list of {@link WebElement} is sorted in descending order.
	 *
	 * @param elements
	 * 		List of {@link WebElement}
	 *
	 * @return if the list is sorted ascending
	 */
	private boolean checkSortedDescending(List<WebElement> elements) {
		boolean isSorted = true;
		for (int i = 1; i < elements.size(); i++) {
			if (elements.get(i).getText().compareTo(elements.get(i - 1).getText()) > 0) {
				isSorted = false;
				break;
			}
		}
		return isSorted;
	}
}
