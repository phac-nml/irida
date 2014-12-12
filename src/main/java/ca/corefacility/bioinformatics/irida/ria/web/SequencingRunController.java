package ca.corefacility.bioinformatics.irida.ria.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;

/**
 * Controller for displaying and interacting with {@link SequencingRun} objects
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@Controller
@RequestMapping("/sequencingRuns")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class SequencingRunController {

	public static final String LIST_VIEW = "sequencingRuns/list";

	private final SequencingRunService sequencingRunService;

	@Autowired
	public SequencingRunController(SequencingRunService sequencingRunService) {
		this.sequencingRunService = sequencingRunService;
	}

	/**
	 * Display the listing page
	 * 
	 * @return The name of the list view
	 */
	@RequestMapping
	public String getListPage() {
		return LIST_VIEW;
	}

	/**
	 * Get a list of all the sequencing runs
	 * 
	 * @return A Collection of Maps contaning sequencing run params
	 */
	@RequestMapping(value = "/ajax/list", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Iterable<SequencingRun> getSequencingRuns() {
		return sequencingRunService.findAll();
	}
}
