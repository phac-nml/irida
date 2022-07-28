package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.sequencingRuns.dto.*;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

/**
 * UI Service for handling requests related to {@link SequencingRun}s
 */
@Component
public class UISequencingRunService {
	private final SequencingRunService runService;
	private final SequencingObjectService objectService;
	private final ProjectService projectService;
	private final SampleService sampleService;
	private final SequenceFileService sequenceFileService;
	private final MessageSource messageSource;

	@Autowired
	public UISequencingRunService(SequencingRunService runService, SequencingObjectService objectService,
			ProjectService projectService, SampleService sampleService, SequenceFileService sequenceFileService,
			MessageSource messageSource) {
		this.runService = runService;
		this.objectService = objectService;
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.sequenceFileService = sequenceFileService;
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
	 * Update or create new samples with sequence files
	 *
	 * @param request - details about the samples
	 * @return a success message
	 */
	public String createSamples(CreateSampleRequest request) {
		List<SampleModel> sampleModelList = request.getSamples();
		for (SampleModel sampleModel : sampleModelList) {
			Sample sample;
			Long sampleId = sampleModel.getSampleId();
			if (sampleId != null) {
				sample = sampleService.read(sampleId);
			} else {
				Long projectId = sampleModel.getProjectId();
				Project project = projectService.read(projectId);
				String sampleName = sampleModel.getSampleName();
				sample = new Sample(sampleName);
				projectService.addSampleToProject(project, sample, true);
			}

			List<SequenceFilePairModel> sequenceFilePairModelList = sampleModel.getPairs();
			for (SequenceFilePairModel sequenceFilePairModel : sequenceFilePairModelList) {
				Long forwardFileId = sequenceFilePairModel.getForward().getId();
				Long reverseFileId = sequenceFilePairModel.getReverse().getId();

				SequenceFile forwardFile = sequenceFileService.read(forwardFileId);
				if (reverseFileId != null) {
					SequenceFile reverseFile = sequenceFileService.read(reverseFileId);
					objectService.createSequencingObjectInSample(new SequenceFilePair(forwardFile, reverseFile),
							sample);
				} else {
					objectService.createSequencingObjectInSample(new SingleEndSequenceFile(forwardFile), sample);
				}

				//fast5?
			}
		}
		return "SUCCESS";
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

