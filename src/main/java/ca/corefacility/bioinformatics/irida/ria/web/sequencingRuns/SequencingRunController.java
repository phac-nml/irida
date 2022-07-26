package ca.corefacility.bioinformatics.irida.ria.web.sequencingRuns;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;

/**
 * Controller for displaying and interacting with {@link SequencingRun} objects
 */
@Controller
@RequestMapping("/sequencing-runs")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TECHNICIAN')")
public class SequencingRunController {

	/**
	 * Display the listing page
	 *
	 * @return The name of the list view
	 */
	@RequestMapping("/**")
	public String getPage() {
		return "sequencing-runs/index";
	}

}
