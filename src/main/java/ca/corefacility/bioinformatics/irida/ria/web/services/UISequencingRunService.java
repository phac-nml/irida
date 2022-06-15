package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.sequencingRuns.dto.SequenceFileDetails;
import ca.corefacility.bioinformatics.irida.ria.web.sequencingRuns.dto.SequencingRunDetails;
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
	 * Get the details for a specific sequencing run.
	 *
	 * @param runId - the id of the sequencing run
	 * @return {@link SequencingRunDetails}
	 */
	public SequencingRunDetails getSequencingRunDetails(Long runId) {
		SequencingRun run = runService.read(runId);
		return new SequencingRunDetails(run);
	}

	/**
	 * Get the files for a specific sequencing run.
	 *
	 * @param runId - the id of the sequencing run
	 * @return a list of {@link SequenceFileDetails}s
	 */
	public List<SequenceFileDetails> getSequencingRunFiles(Long runId) {
		SequencingRun run = runService.read(runId);
		Set<SequencingObject> sequencingObjects = objectService.getSequencingObjectsForSequencingRun(run);

		List<SequenceFileDetails> response = new ArrayList<>();
		for (SequencingObject object : sequencingObjects) {
			Set<SequenceFile> files = object.getFiles();
			for (SequenceFile file : files) {
				response.add(new SequenceFileDetails(file, object.getId()));
			}
		}
		Collections.sort(response);
		return response;
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
					messageSource.getMessage("server.sequencingruns.status." + run.getUploadStatus().toString(),
							new Object[] {}, locale)));
		}

		return new TableResponse<>(runs, list.getTotalElements());
	}

	/**
	 * Delete a sequencing run.
	 *
	 * @param runId  - the id of the sequencing run
	 * @param locale - current users locale
	 * @return a success message
	 */
	public String deleteSequencingRun(Long runId, Locale locale) {
		runService.delete(runId);
		return messageSource.getMessage("server.sequencingruns.delete.success", new Object[] { runId }, locale);
	}
}

