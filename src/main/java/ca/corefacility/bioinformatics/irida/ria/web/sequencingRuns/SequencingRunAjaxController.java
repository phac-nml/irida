package ca.corefacility.bioinformatics.irida.ria.web.sequencingRuns;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.sequencingRuns.dto.SequencingRunModel;
import ca.corefacility.bioinformatics.irida.ria.web.sequencingRuns.dto.SequencingRunsListRequest;
import ca.corefacility.bioinformatics.irida.ria.web.services.UISequencingRunService;

/**
 * Controller to handle AJAX requests for sequencing run data
 */
@RestController
@RequestMapping("/ajax/sequencingRuns")
public class SequencingRunAjaxController {

	private final UISequencingRunService service;

	@Autowired
	public SequencingRunAjaxController(UISequencingRunService service) {
		this.service = service;
	}

	/**
	 * Get the details for a specific sequencing run.
	 *
	 * @param runId - the id of the sequencing run
	 * @return a {@link SequencingRun}
	 */
	@RequestMapping("/{runId}")
	public ResponseEntity<SequencingRun> getSequencingRun(@PathVariable("runId") Long runId) {
		return ResponseEntity.ok(service.getSequencingRun(runId));
	}

	/**
	 * Get the current page contents for a table displaying sequencing runs.
	 *
	 * @param sequencingRunsListRequest {@link SequencingRunsListRequest} specifies what data is required.
	 * @param locale                    {@link Locale}
	 * @return {@link TableResponse}
	 */
	@RequestMapping("/list")
	public TableResponse<SequencingRunModel> listSequencingRuns(
			@RequestBody SequencingRunsListRequest sequencingRunsListRequest, Locale locale) {
		return service.listSequencingRuns(sequencingRunsListRequest, locale);
	}
}
