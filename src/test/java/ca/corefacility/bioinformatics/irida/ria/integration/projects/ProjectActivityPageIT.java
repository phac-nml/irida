package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import org.junit.Assert;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.ProjectActivityPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

/**
 * <p> Integration test to ensure that the Project Details Page. </p>
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class ProjectActivityPageIT extends AbstractIridaUIITChromeDriver {

	@Test
	public void testDisplaysProjectEvent() {
		LoginPage.loginAsManager(driver());
		ProjectActivityPage page = ProjectActivityPage.goTo(driver());
		Assert.assertEquals("Should be activities", 1, page.getNumberOfActivities());
		Assert.assertEquals("Should be a add user role event", "project_user_role_updated", page.getActivityTypeForActivity(0));
		Assert.assertFalse("Load more activities button should be disabled", page.isLoadMoreButtonEnabled());
	}
}