package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.sequencingRuns.dto.SequenceFileDetails;
import ca.corefacility.bioinformatics.irida.ria.web.sequencingRuns.dto.SequencingRunModel;
import ca.corefacility.bioinformatics.irida.ria.web.sequencingRuns.dto.SequencingRunsListRequest;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;

/**
 * UI Service for handling requests related to {@link SequencingRun}s
 */
@Component
public class UISequencingRunService {
	private final SequencingRunService runService;
	private final SequencingObjectService objectService;
	private final MessageSource messageSource;

	@Autowired
	public UISequencingRunService(SequencingRunService runService, SequencingObjectService objectService,
			MessageSource messageSource) {
		this.runService = runService;
		this.objectService = objectService;
		this.messageSource = messageSource;
	}

	/**
	 * Get the details for a specific sequencing run.
	 *
	 * @param runId - the id of the sequencing run
	 * @return {@link SequencingRun}
	 */
	public SequencingRun getSequencingRun(Long runId) {
		return runService.read(runId);
	}

	/**
	 * Get the files for a specific sequencing run.
	 *
	 * @param runId - the id of the sequencing run
	 * @return a set of {@link SequencingObject}s
	 */
	public Set<SequencingObject> getSequencingRunFiles(Long runId) {
		SequencingRun run = runService.read(runId);
		return objectService.getSequencingObjectsForSequencingRun(run);
	}

	/**
	 * Get the files for a specific sequencing run.
	 *
	 * @param runId - the id of the sequencing run
	 * @return a list of {@link SequenceFileDetails}s
	 */
	public List<SequenceFileDetails> getSequencingRunFiles2(Long runId) {
		SequencingRun run = runService.read(runId);
		return objectService.getSequencingObjectsForSequencingRun2(run);
	}

	/**
	 * Get the current page contents for a table displaying sequencing runs.
	 *
	 * @param sequencingRunsListRequest {@link SequencingRunsListRequest} specifies what data is required.
	 * @param locale                    {@link Locale}
	 * @return {@link TableResponse}
	 */
	public TableResponse<SequencingRunModel> listSequencingRuns(SequencingRunsListRequest sequencingRunsListRequest,
			Locale locale) {
		Page<SequencingRun> list = runService.list(sequencingRunsListRequest.getCurrent(),
				sequencingRunsListRequest.getPageSize(), sequencingRunsListRequest.getSort());

		List<SequencingRunModel> runs = new ArrayList<>();
		for (SequencingRun run : list.getContent()) {
			runs.add(new SequencingRunModel(run,
					messageSource.getMessage("sequencingruns.status." + run.getUploadStatus().toString(),
							new Object[] {}, locale)));
		}

		return new TableResponse<>(runs, list.getTotalElements());
	}

	/**
	 * Delete a sequencing run.
	 *
	 * @param runId - the id of the sequencing run
	 */
	public void deleteSequencingRun(Long runId) {
		runService.delete(runId);
	}
}

