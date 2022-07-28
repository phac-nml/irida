package ca.corefacility.bioinformatics.irida.ria.web.sequencingRuns;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.sequencingRuns.dto.*;
import ca.corefacility.bioinformatics.irida.ria.web.services.UISequencingRunService;

/**
 * Controller to handle AJAX requests for sequencing run data
 */
@RestController
@RequestMapping("/ajax/sequencing-runs")
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
	public ResponseEntity<SequencingRun> getSequencingRun(@PathVariable long runId) {
		return ResponseEntity.ok(service.getSequencingRun(runId));
	}

	/**
	 * Get the details for a specific sequencing run.
	 *
	 * @param runId - the id of the sequencing run
	 * @return a {@link SequencingRunDetails}
	 */
	@RequestMapping("/{runId}/details")
	public ResponseEntity<SequencingRunDetails> getSequencingRunDetails(@PathVariable long runId) {
		return ResponseEntity.ok(service.getSequencingRunDetails(runId));
	}

	/**
	 * Get the files for a specific sequencing run.
	 *
	 * @param runId - the id of the sequencing run
	 * @return a list of {@link SequenceFileDetails}s
	 */
	@RequestMapping("/{runId}/sequenceFiles")
	public ResponseEntity<List<SequenceFileDetails>> getSequencingRunFiles(@PathVariable long runId) {
		return ResponseEntity.ok(service.getSequencingRunFiles(runId));
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

	/**
	 * Update or create new samples with sequence files
	 *
	 * @param request - details about the samples
	 * @return a success message
	 */
	@PostMapping("/samples")
	public ResponseEntity<String> createSamples(@RequestBody CreateSampleRequest request) {
		return ResponseEntity.ok(service.createSamples(request));
	}

	/**
	 * Delete a sequencing run.
	 *
	 * @param runId  - the id of the sequencing run
	 * @param locale - current users locale return Message to user about what happened
	 * @return a success message
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping("/{runId}")
	public ResponseEntity<String> deleteSequencingRun(@PathVariable long runId, Locale locale) {
		return ResponseEntity.ok(service.deleteSequencingRun(runId, locale));
	}

}
