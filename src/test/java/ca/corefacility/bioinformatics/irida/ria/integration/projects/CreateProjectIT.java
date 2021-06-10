package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;

import com.github.springtestdbunit.annotation.DatabaseSetup;

/**
 * <p>
 * Integration test to ensure that the ProjectsNew Page.
 * </p>
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class CreateProjectIT extends AbstractIridaUIITChromeDriver {

}
