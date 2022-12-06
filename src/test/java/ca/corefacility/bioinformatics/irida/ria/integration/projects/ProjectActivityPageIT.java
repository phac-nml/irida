package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.ProjectActivityPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * <p> Integration test to ensure that the Project Details Page. </p>
 */
@Disabled
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class ProjectActivityPageIT extends AbstractIridaUIITChromeDriver {

	@Test
	public void testDisplaysProjectEvent() {
		LoginPage.loginAsManager(driver());
		ProjectActivityPage page = ProjectActivityPage.goTo(driver());
		assertEquals(1, page.getNumberOfActivities(), "Should be activities");
		assertEquals("project_user_role_updated", page.getActivityTypeForActivity(0), "Should be a add user role event");
		assertFalse(page.isLoadMoreButtonEnabled(), "Load more activities button should be disabled");
	}
}