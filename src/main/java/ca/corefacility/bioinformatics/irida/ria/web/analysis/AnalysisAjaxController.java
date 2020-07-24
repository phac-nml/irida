package ca.corefacility.bioinformatics.irida.ria.web.analysis;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.config.analysis.ExecutionManagerConfig;
import ca.corefacility.bioinformatics.irida.exceptions.*;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectMetadataTemplateJoin;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.*;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.ProjectAnalysisSubmissionJoin;
import ca.corefacility.bioinformatics.irida.pipeline.results.AnalysisSubmissionSampleProcessor;
import ca.corefacility.bioinformatics.irida.ria.utilities.FileUtilities;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.auditing.AnalysisAudit;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.dto.*;
import ca.corefacility.bioinformatics.irida.ria.web.components.AnalysisOutputFileDownloadManager;
import ca.corefacility.bioinformatics.irida.ria.web.dto.ExcelData;
import ca.corefacility.bioinformatics.irida.ria.web.dto.ResponseDetails;
import ca.corefacility.bioinformatics.irida.ria.web.utilities.DateUtilities;
import ca.corefacility.bioinformatics.irida.security.permissions.analysis.UpdateAnalysisSubmissionPermission;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;
import ca.corefacility.bioinformatics.irida.model.project.Project;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jsonldjava.shaded.com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

/**
 * Controller for individual Analysis ajax requests (details page,
 * analysis outputs, project analysis outputs)
 */
@RestController
@Scope("session")
@RequestMapping("/ajax/analysis")
public class AnalysisAjaxController {
	private static final Logger logger = LoggerFactory.getLogger(AnalysisAjaxController.class);
	private static final String TREE_EXT = "newick";
	private static final String EMPTY_TREE = "();";

	private AnalysisSubmissionService analysisSubmissionService;
	private IridaWorkflowsService workflowsService;
	private MessageSource messageSource;
	private AnalysisSubmissionSampleProcessor analysisSubmissionSampleProcessor;
	private AnalysisOutputFileDownloadManager analysisOutputFileDownloadManager;
	private MetadataTemplateService metadataTemplateService;
	private SequencingObjectService sequencingObjectService;
	private SampleService sampleService;
	private ProjectService projectService;
	private UserService userService;
	private UpdateAnalysisSubmissionPermission updateAnalysisPermission;
	private ExecutionManagerConfig configFile;
	private AnalysisAudit analysisAudit;

	@Autowired
	public AnalysisAjaxController(AnalysisSubmissionService analysisSubmissionService,
			IridaWorkflowsService iridaWorkflowsService, UserService userService, SampleService sampleService,
			ProjectService projectService, UpdateAnalysisSubmissionPermission updateAnalysisPermission, MetadataTemplateService metadataTemplateService,
			SequencingObjectService sequencingObjectService,
			AnalysisSubmissionSampleProcessor analysisSubmissionSampleProcessor,
			AnalysisOutputFileDownloadManager analysisOutputFileDownloadManager, MessageSource messageSource, ExecutionManagerConfig configFile, AnalysisAudit analysisAudit) {

		this.analysisSubmissionService = analysisSubmissionService;
		this.workflowsService = iridaWorkflowsService;
		this.userService = userService;
		this.analysisOutputFileDownloadManager = analysisOutputFileDownloadManager;
		this.messageSource = messageSource;
		this.sampleService = sampleService;
		this.projectService = projectService;
		this.metadataTemplateService = metadataTemplateService;
		this.sequencingObjectService = sequencingObjectService;
		this.analysisSubmissionSampleProcessor = analysisSubmissionSampleProcessor;
		this.updateAnalysisPermission = updateAnalysisPermission;
		this.configFile = configFile;
		this.analysisAudit = analysisAudit;
	}

	/**
	 * Get all {@link User} generated {@link AnalysisOutputFile} info for principal User
	 * @param principal Principal {@link User}
	 * @return {@link User} generated {@link AnalysisOutputFile} info
	 */
	@RequestMapping(value = "/user/analysis-outputs")
	@ResponseBody
	public List<ProjectSampleAnalysisOutputInfo> getAllUserAnalysisOutputInfo(Principal principal) {
		final User user = userService.getUserByUsername(principal.getName());
		return analysisSubmissionService.getAllUserAnalysisOutputInfo(user);
	}

	/**
	 * Get all {@link User} generated {@link AnalysisOutputFile} info
	 * @param userId {@link User} id
	 * @return {@link User} generated {@link AnalysisOutputFile} info
	 */
	@RequestMapping(value = "/user/{userId}/analysis-outputs")
	@ResponseBody
	public List<ProjectSampleAnalysisOutputInfo> getAllUserAnalysisOutputInfo(@PathVariable Long userId) {
		final User user = userService.read(userId);
		return analysisSubmissionService.getAllUserAnalysisOutputInfo(user);
	}


	/**
	 * Get analysis output file information for all analyses shared with a {@link Project}.
	 *
	 * @param projectId {@link Project} id
	 * @return list of {@link ProjectSampleAnalysisOutputInfo}
	 */
	@RequestMapping(value = "/project/{projectId}/shared-analysis-outputs")
	@ResponseBody
	public List<ProjectSampleAnalysisOutputInfo> getAllAnalysisOutputInfoSharedWithProject(@PathVariable Long projectId) {
		return analysisSubmissionService.getAllAnalysisOutputInfoSharedWithProject(projectId);
	}

	/**
	 * Get analysis output file information for all automated analyses for a {@link Project}.
	 *
	 * @param projectId {@link Project} id
	 * @return list of {@link ProjectSampleAnalysisOutputInfo}
	 */
	@RequestMapping(value = "/project/{projectId}/automated-analysis-outputs")
	@ResponseBody
	public List<ProjectSampleAnalysisOutputInfo> getAllAutomatedAnalysisOutputInfoForAProject(@PathVariable Long projectId) {
		return analysisSubmissionService.getAllAutomatedAnalysisOutputInfoForAProject(projectId);
	}

	/**
	 * Update an analysis email pipeline completion result
	 *
	 * @param parameters parameters which include the submission id and
	 *                   the new email pipeline result value
	 * @param locale     User's locale
	 * @param response   HTTP response object
	 * @return dto with message
	 */
	@RequestMapping(value = "/update-email-pipeline-result", method = RequestMethod.PATCH)
	public ResponseDetails ajaxUpdateEmailPipelineResult(@RequestBody AnalysisEmailPipelineResult parameters,
			Locale locale, HttpServletResponse response) {

		AnalysisSubmission submission = analysisSubmissionService.read(parameters.getAnalysisSubmissionId());
		String message = "";

		if ((submission.getAnalysisState() != AnalysisState.COMPLETED) && (submission.getAnalysisState()
				!= AnalysisState.ERROR)) {
			submission.setEmailPipelineResult(parameters.getEmailPipelineResult());
			analysisSubmissionService.update(submission);
			logger.trace("Email pipeline result updated for: " + submission);

			if (parameters.getEmailPipelineResult()) {
				message = messageSource.getMessage("AnalysisDetails.willReceiveEmail", new Object[] {}, locale);
			} else {
				message = messageSource.getMessage("AnalysisDetails.willNotReceiveEmail", new Object[] {}, locale);
			}
		} else {
			logger.debug("Email on completion preference not updated due to analysis state");
			message = messageSource.getMessage("AnalysisDetails.emailOnPipelineResultNotUpdated", new Object[] {},
					locale);
			response.setStatus(422);
		}
		return new ResponseDetails(message);
	}

	/**
	 * Get analysis details
	 *
	 * @param submissionId analysis submission id to get data for
	 * @param locale       User's locale
	 * @param response     HTTP response object
	 * @return dto of analysis details
	 */
	@RequestMapping(value = "/details/{submissionId}", method = RequestMethod.GET)
	public AnalysisDetails ajaxGetDataForDetailsTab(@PathVariable Long submissionId, Locale locale,
			HttpServletResponse response) {
		logger.trace("reading analysis submission " + submissionId);
		AnalysisSubmission submission = analysisSubmissionService.read(submissionId);
		IridaWorkflow iridaWorkflow = workflowsService.getIridaWorkflowOrUnknown(submission);

		// Get the name of the workflow
		AnalysisType analysisType = iridaWorkflow.getWorkflowDescription()
				.getAnalysisType();
		String workflowName = messageSource.getMessage("workflow." + analysisType.getType() + ".title", null,
				analysisType.getType(), locale);

		String version = iridaWorkflow.getWorkflowDescription()
				.getVersion();
		String priority = submission.getPriority()
				.toString();

		// Get the run time of the analysis runtime using the analysis
		Long duration;
		if(submission.getAnalysisState() != AnalysisState.COMPLETED && submission.getAnalysisState() != AnalysisState.ERROR) {
			Date currentDate = new Date();
			duration = DateUtilities.getDurationInMilliseconds(submission.getCreatedDate(), currentDate);
		} else {
			duration = analysisAudit.getAnalysisRunningTime(submission);
		}

		AnalysisSubmission.Priority[] priorities = AnalysisSubmission.Priority.values();
		boolean emailPipelineResult = submission.getEmailPipelineResult();

		boolean canShareToSamples = false;
		if (submission.getAnalysis() != null) {
			canShareToSamples = analysisSubmissionSampleProcessor.hasRegisteredAnalysisSampleUpdater(
					submission.getAnalysis()
							.getAnalysisType());
		}
		String analysisDescription = submission.getAnalysisDescription();
		// Check if user can update analysis
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();

		response.setStatus(HttpServletResponse.SC_OK);

		// details is a DTO (Data Transfer Object)
		return new AnalysisDetails(analysisDescription, workflowName, version, priority, duration, submission.getCreatedDate(), priorities,
				emailPipelineResult, canShareToSamples, updateAnalysisPermission.isAllowed(authentication, submission),
				submission.getUpdateSamples());
	}

	/**
	 * Get analysis input files and their sizes
	 *
	 * @param submissionId analysis submission id to get data for
	 * @param locale User's locale
	 * @return dto of analysis input files data
	 */
	@RequestMapping(value = "/inputs/{submissionId}", method = RequestMethod.GET)
	public AnalysisInputFiles ajaxGetAnalysisInputFiles(@PathVariable Long submissionId, Locale locale) {
		logger.trace("reading analysis submission " + submissionId);
		AnalysisSubmission submission = analysisSubmissionService.read(submissionId);
		ReferenceFile referenceFile = null;

		Set<SequenceFilePair> inputFilePairs = sequencingObjectService.getSequencingObjectsOfTypeForAnalysisSubmission(
				submission, SequenceFilePair.class);

		List<SampleSequencingObject> sampleFiles = inputFilePairs.stream()
				.map(SampleSequencingObject::new)
				.sorted()
				.collect(Collectors.toList());


		// - Single
		Set<SingleEndSequenceFile> inputFilesSingle = sequencingObjectService.getSequencingObjectsOfTypeForAnalysisSubmission(
				submission, SingleEndSequenceFile.class);
		List<SampleSequencingObject> singleFiles = inputFilesSingle.stream()
				.map(SampleSequencingObject::new)
				.sorted()
				.collect(Collectors.toList());

		IridaWorkflow iridaWorkflow = workflowsService.getIridaWorkflowOrUnknown(submission);

		if (iridaWorkflow != null && iridaWorkflow.getWorkflowDescription()
				.requiresReference() && submission.getReferenceFile()
				.isPresent()) {

			referenceFile = submission.getReferenceFile()
					.get();
		} else {
			logger.debug("No reference file required for workflow.");
		}

		//List of AnalysisSamples which store the sample info
		List<AnalysisSamples> pairedEnd = new ArrayList<>();
		List<AnalysisSingleEndSamples> singleEnd = new ArrayList<>();

		for(SampleSequencingObject sso : sampleFiles) {
			SequenceFilePair fp = (SequenceFilePair)sso.getSequencingObject();
			if(fp.getFiles().size() == 2) {
				String sampleName = messageSource.getMessage("AnalysisSamples.sampleDeleted",
						new Object[] {}, locale);
				Long sampleId = 0L;
				if(sso.getSample() != null) {
					sampleName = sso.getSample().getSampleName();
					sampleId = sso.getSample().getId();
				}
				pairedEnd.add(new AnalysisSamples(sampleName, sampleId, fp.getId(), fp.getForwardSequenceFile(), fp.getReverseSequenceFile()));
			}
		}

		for(SampleSequencingObject sso : singleFiles) {
			SingleEndSequenceFile sesf = (SingleEndSequenceFile)sso.getSequencingObject();
			if(sesf.getFiles().size() == 1) {
				String sampleName = messageSource.getMessage("AnalysisSamples.sampleDeleted",
						new Object[] {}, locale);
				Long sampleId = 0L;
				if(sso.getSample() != null) {
					sampleName = sso.getSample().getSampleName();
					sampleId = sso.getSample().getId();
				}
				singleEnd.add(new AnalysisSingleEndSamples(sampleName, sampleId, sesf.getId(), sesf.getSequenceFile()));
			}
		}

		return new AnalysisInputFiles(pairedEnd, singleEnd, referenceFile);
	}

	/**
	 * Update an analysis name and/or priority
	 *
	 * @param parameters parameters which include the submission id and the new name and/or priority
	 * @param locale     User's locale
	 * @param response   HTTP response object
	 * @return dto with message
	 */
	@RequestMapping(value = "/update-analysis", method = RequestMethod.PATCH)
	public ResponseDetails ajaxUpdateSubmission(@RequestBody AnalysisSubmissionInfo parameters, Locale locale,
			HttpServletResponse response) {
		AnalysisSubmission submission = analysisSubmissionService.read(parameters.getAnalysisSubmissionId());
		String message = "";

		if (parameters.getAnalysisName() != null) {
			submission.setName(parameters.getAnalysisName());
			message = messageSource.getMessage("AnalysisDetails.nameUpdated",
					new Object[] { parameters.getAnalysisName() }, locale);
			analysisSubmissionService.update(submission);
		} else if (parameters.getPriority() != null) {
			if (submission.getAnalysisState() == AnalysisState.NEW) {
				submission.setPriority(parameters.getPriority());
				analysisSubmissionService.updatePriority(submission, parameters.getPriority());
				message = messageSource.getMessage("AnalysisDetails.priorityUpdated",
						new Object[] { parameters.getPriority(), submission.getName() }, locale);
			} else {
				logger.trace("Unable to update priority as: " + submission + "is no longer in queued state");
				message = messageSource.getMessage("AnalysisDetails.priorityNotUpdated", new Object[] {}, locale);
				response.setStatus(422);
			}
		}
		return new ResponseDetails(message);
	}

	/**
	 * For an {@link AnalysisSubmission}, get info about each {@link AnalysisOutputFile}
	 *
	 * @param id {@link AnalysisSubmission} id
	 * @return map of info about each {@link AnalysisOutputFile}
	 */
	@RequestMapping(value = "/{id}/outputs", method = RequestMethod.GET)
	@ResponseBody
	public List<AnalysisOutputFileInfo> getOutputFilesInfo(@PathVariable Long id) {
		AnalysisSubmission submission = analysisSubmissionService.read(id);
		Analysis analysis = submission.getAnalysis();
		List<String> outputNames;
		try {
			outputNames = workflowsService.getOutputNames(submission.getWorkflowId());
		} catch (IridaWorkflowNotFoundException e) {
			outputNames = Lists.newArrayList(analysis.getAnalysisOutputFileNames());
			Collections.sort(outputNames);
		}

		return outputNames.stream()
				.map((outputName) -> getAnalysisOutputFileInfo(submission, analysis, outputName))
				.filter(Objects::nonNull)
				.filter(x -> x.getFileSizeBytes() > 0L)
				.filter(x -> !(TREE_EXT.equals(x.getFileExt()) && EMPTY_TREE.equals(x.getFirstLine())))
				.collect(Collectors.toList());
	}

	/**
	 * Get {@link AnalysisOutputFileInfo}.
	 *
	 * @param submission {@link AnalysisSubmission} of {@code analysis}
	 * @param analysis   {@link Analysis} to get {@link AnalysisOutputFile}s from
	 * @param outputName Workflow output name
	 * @return {@link AnalysisOutputFile} info
	 */
	private AnalysisOutputFileInfo getAnalysisOutputFileInfo(AnalysisSubmission submission, Analysis analysis,
			String outputName) {
		// set of file extensions for indicating whether the first line of the file should be read
		final ImmutableSet<String> FILE_EXT_READ_FIRST_LINE = ImmutableSet.of("tsv", "txt", "tabular", "csv", "tab",
				TREE_EXT);
		final AnalysisOutputFile aof = analysis.getAnalysisOutputFile(outputName);
		if (aof != null) {
			String fileExt = FileUtilities.getFileExt(aof.getFile()
					.getFileName()
					.toString());
			ToolExecution tool = aof.getCreatedByTool();

			AnalysisOutputFileInfo info = new AnalysisOutputFileInfo(aof.getId(), submission.getId(), analysis.getId(),
					aof.getFile()
							.getFileName()
							.toString(), fileExt, aof.getFile()
					.toFile()
					.length(), tool.getToolName(), tool.getToolVersion(), outputName);

			if (FILE_EXT_READ_FIRST_LINE.contains(fileExt)) {
				addFirstLine(info, aof);
			}
			return info;
		}
		return null;
	}


	/**
	 * Add the {@code firstLine} and {@code filePointer} file byte position after reading the first line of an {@link AnalysisOutputFile} to a {@link AnalysisOutputFileInfo} object.
	 *
	 * @param info Object to add {@code firstLine} and {@code filePointer} info to
	 * @param aof {@link AnalysisOutputFile} to read from
	 */
	private void addFirstLine(AnalysisOutputFileInfo info, AnalysisOutputFile aof) {
		RandomAccessFile reader = null;
		final Path aofFile = aof.getFile();
		try {
			reader = new RandomAccessFile(aofFile.toFile(), "r");
			info.setFirstLine(reader.readLine());
			info.setFilePointer(reader.getFilePointer());
		} catch (FileNotFoundException e) {
			logger.error("Could not find file '" + aofFile + "' " + e);
		} catch (IOException e) {
			logger.error("Could not read file '" + aofFile + "' " + e);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				logger.error("Could not close file handle for '" + aofFile + "' " + e);
			}
		}
	}

	/**
	 * Read some lines or text from an {@link AnalysisOutputFile}.
	 *
	 * @param id       {@link AnalysisSubmission} id
	 * @param fileId   {@link AnalysisOutputFile} id
	 * @param limit    Optional limit to number of lines to read from file
	 * @param start    Optional line to start reading from
	 * @param end      Optional line to stop reading at
	 * @param seek     Optional file byte position to seek to and begin reading
	 * @param chunk    Optional number of bytes to read from file
	 * @param response HTTP response object
	 * @return JSON with file text or lines as well as information about the file.
	 */
	@RequestMapping(value = "/{id}/outputs/{fileId}", method = RequestMethod.GET)
	@ResponseBody
	public AnalysisOutputFileInfo getOutputFile(@PathVariable Long id, @PathVariable Long fileId,
			@RequestParam(defaultValue = "100", required = false) Long limit,
			@RequestParam(required = false) Long start, @RequestParam(required = false) Long end,
			@RequestParam(defaultValue = "0", required = false) Long seek, @RequestParam(required = false) Long chunk,
			HttpServletResponse response) {
		AnalysisSubmission submission = analysisSubmissionService.read(id);
		Analysis analysis = submission.getAnalysis();
		final Optional<AnalysisOutputFile> analysisOutputFile = analysis.getAnalysisOutputFiles()
				.stream()
				.filter(x -> Objects.equals(x.getId(), fileId))
				.findFirst();
		if (analysisOutputFile.isPresent()) {
			final AnalysisOutputFile aof = analysisOutputFile.get();
			final Path aofFile = aof.getFile();
			final ToolExecution tool = aof.getCreatedByTool();
			final AnalysisOutputFileInfo contents = new AnalysisOutputFileInfo();
			contents.setId(aof.getId());
			contents.setAnalysisSubmissionId(submission.getId());
			contents.setAnalysisId(analysis.getId());
			contents.setFilename(aofFile.getFileName()
					.toString());
			contents.setFileExt(FileUtilities.getFileExt(aofFile.getFileName()
					.toString()));
			contents.setFileSizeBytes(aof.getFile()
					.toFile()
					.length());
			contents.setToolName(tool.getToolName());
			contents.setToolVersion(tool.getToolVersion());
			try {
				final File file = aofFile.toFile();
				final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
				randomAccessFile.seek(seek);
				if (seek == 0) {
					if (chunk != null && chunk > 0) {
						contents.setText(FileUtilities.readChunk(randomAccessFile, seek, chunk));
						contents.setChunk(chunk);
						contents.setStartSeek(seek);
					} else {
						final BufferedReader reader = new BufferedReader(new FileReader(randomAccessFile.getFD()));
						final List<String> lines = FileUtilities.readLinesLimit(reader, limit, start, end);
						contents.setLines(lines);
						contents.setLimit((long) lines.size());
						contents.setStart(start);
						contents.setEnd(start + lines.size());
					}
				} else {
					if (chunk != null && chunk > 0) {
						contents.setText(FileUtilities.readChunk(randomAccessFile, seek, chunk));
						contents.setChunk(chunk);
						contents.setStartSeek(seek);
					} else {
						final List<String> lines = FileUtilities.readLinesFromFilePointer(randomAccessFile, limit);
						contents.setLines(lines);
						contents.setStartSeek(seek);
						contents.setStart(start);
						contents.setLimit((long) lines.size());
					}
				}
				contents.setFilePointer(randomAccessFile.getFilePointer());
			} catch (IOException e) {
				logger.error("Could not read output file '" + aof.getId() + "' " + e);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				contents.setError("Could not read output file");

			}
			return contents;
		} else {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
	}

	/**
	 * Get a dto with list of {@link JobError} for an {@link AnalysisSubmission} under key `galaxyJobErrors`
	 * and the `galaxyUrl` for the galaxy instance
	 *
	 * @param submissionId {@link AnalysisSubmission} id
	 * @return dto with galaxyJobErrors and galaxyUrl
	 */
	@RequestMapping(value = "/{submissionId}/job-errors", method = RequestMethod.GET)
	@ResponseBody
	public AnalysisJobError ajaxGetJobErrors(@PathVariable Long submissionId) {
		try {
			AnalysisSubmission submission = analysisSubmissionService.read(submissionId);
			String galaxyHistoryId = submission.getRemoteAnalysisId();
			List<JobError> galaxyJobErrors = analysisSubmissionService.getJobErrors(submissionId);
			String galaxyUrl = "";
			try {
				galaxyUrl = configFile.galaxyInstance()
						.getGalaxyUrl();
			} catch (ExecutionManagerConfigurationException e) {
				logger.error("Error " + e);
			}
			// Return a dto with galaxyJobErrors, galaxyUrl, and galaxyHistroyId
			if (galaxyJobErrors != null && !galaxyJobErrors.isEmpty()) {
				return new AnalysisJobError(galaxyJobErrors, galaxyUrl, galaxyHistoryId);
			} else if(galaxyHistoryId != null) {
				return new AnalysisJobError(null, galaxyUrl, galaxyHistoryId);
			}
		} catch (ExecutionManagerException e) {
			logger.error("Error " + e);
		}
		// Return a dto with galaxyJobErrors, galaxyUrl, and galaxyHistoryId set to null
		return new AnalysisJobError();
	}

	/**
	 * Get the status of projects that can be shared with the given analysis
	 *
	 * @param submissionId
	 *            the {@link AnalysisSubmission} id
	 * @return a list of {@link SharedProjectResponse}
	 */
	@RequestMapping(value = "/{submissionId}/share", method = RequestMethod.GET)
	@ResponseBody
	public List<SharedProjectResponse> getSharedProjectsForAnalysis(@PathVariable Long submissionId) {
		AnalysisSubmission submission = analysisSubmissionService.read(submissionId);

		// Input files

		// - Paired
		Set<SequenceFilePair> inputFilePairs = sequencingObjectService.getSequencingObjectsOfTypeForAnalysisSubmission(
				submission, SequenceFilePair.class);

		// Single End
		Set<SingleEndSequenceFile> inputFileSingleEnd = sequencingObjectService.getSequencingObjectsOfTypeForAnalysisSubmission(
				submission, SingleEndSequenceFile.class);

		// get projects already shared with submission
		Set<Project> projectsShared = projectService.getProjectsForAnalysisSubmission(submission)
				.stream()
				.map(ProjectAnalysisSubmissionJoin::getSubject)
				.collect(Collectors.toSet());

		// get available projects
		Set<Project> projectsInAnalysisPaired = projectService.getProjectsForSequencingObjects(inputFilePairs);
		Set<Project> projectsInAnalysisSingleEnd = projectService.getProjectsForSequencingObjects(inputFileSingleEnd);

		// Create response for shared projects
		List<SharedProjectResponse> projectResponses = projectsShared.stream()
				.map(p -> new SharedProjectResponse(p, true))
				.collect(Collectors.toList());

		projectResponses.addAll(projectsInAnalysisPaired.stream()
				.filter(p -> !projectsShared.contains(p))
				.map(p -> new SharedProjectResponse(p, false))
				.collect(Collectors.toList()));

		projectResponses.addAll(projectsInAnalysisSingleEnd.stream()
				.filter(p -> !projectsShared.contains(p))
				.map(p -> new SharedProjectResponse(p, false))
				.collect(Collectors.toList()));


		projectResponses.sort(new Comparator<SharedProjectResponse>() {

			@Override
			public int compare(SharedProjectResponse p1, SharedProjectResponse p2) {
				return p1.getProject()
						.getName()
						.compareTo(p2.getProject()
								.getName());
			}
		});

		return projectResponses;
	}

	/**
	 * Update the share status of a given {@link AnalysisSubmission} for a given
	 * {@link Project}
	 *
	 * @param submissionId the {@link AnalysisSubmission} id to share/unshare
	 * @param projectShare {@link AnalysisProjectShare} describes of the project and the share status.
	 * @param locale       Locale of the logged in user
	 * @return Success message if successful
	 */
	@RequestMapping(value = "/{submissionId}/share", method = RequestMethod.POST)
	public ResponseDetails updateProjectShare(@PathVariable Long submissionId,
			@RequestBody AnalysisProjectShare projectShare, Locale locale) {
		AnalysisSubmission submission = analysisSubmissionService.read(submissionId);
		Project project = projectService.read(projectShare.getProjectId());

		String message = "";
		if (projectShare.isShareStatus()) {
			analysisSubmissionService.shareAnalysisSubmissionWithProject(submission, project);

			message = messageSource.getMessage("analysis.details.share.enable", new Object[] { project.getLabel() },
					locale);
		} else {
			analysisSubmissionService.removeAnalysisProjectShare(submission, project);
			message = messageSource.getMessage("analysis.details.share.remove", new Object[] { project.getLabel() },
					locale);
		}

		return new ResponseDetails(message);

	}

	/**
	 * Save the results of an analysis back to the samples
	 *
	 * @param submissionId ID of the {@link AnalysisSubmission}
	 * @param locale       locale of the logged in user
	 * @param response     HTTP response object
	 * @return success message
	 */
	@RequestMapping(value = "/{submissionId}/save-results", method = RequestMethod.POST)
	@ResponseBody
	public ResponseDetails saveResultsToSamples(@PathVariable Long submissionId, Locale locale,
			HttpServletResponse response) {
		AnalysisSubmission submission = analysisSubmissionService.read(submissionId);

		String message = messageSource.getMessage("analysis.details.save.response", null, locale);

		if (submission.getUpdateSamples()) {
			message = messageSource.getMessage("analysis.details.save.alreadysavederror", null, locale);
			response.setStatus(422);
		}

		try {
			analysisSubmissionSampleProcessor.updateSamples(submission);
			submission.setUpdateSamples(true);
			analysisSubmissionService.update(submission);
		} catch (PostProcessingException e) {
			if (e.toString()
					.contains("Expected one sample; got '0' for analysis [id=" + submissionId + "]")) {
				message = messageSource.getMessage("AnalysisShare.noSamplesToSaveResults", null, locale);
			} else {
				message = messageSource.getMessage("analysis.details.save.processingerror", null, locale);
			}
			response.setStatus(422);
		}

		return new ResponseDetails(message);
	}

	/**
	 * Get the sistr analysis information to display
	 *
	 * @param id ID of the analysis submission
	 * @return dto with SISTR analysis results
	 */
	@SuppressWarnings("resource")
	@RequestMapping("/sistr/{id}")
	@ResponseBody
	public AnalysisSistrResults getSistrAnalysis(@PathVariable Long id) {
		AnalysisSubmission submission = analysisSubmissionService.read(id);
		Collection<Sample> samples = sampleService.getSamplesForAnalysisSubmission(submission);

		final String sistrFileKey = "sistr-predictions";

		// Get details about the workflow
		UUID workflowUUID = submission.getWorkflowId();
		IridaWorkflow iridaWorkflow;
		try {
			iridaWorkflow = workflowsService.getIridaWorkflow(workflowUUID);
		} catch (IridaWorkflowNotFoundException e) {
			logger.error("Error finding workflow, ", e);
			throw new EntityNotFoundException("Couldn't find workflow for submission " + submission.getId(), e);
		}
		AnalysisType analysisType = iridaWorkflow.getWorkflowDescription()
				.getAnalysisType();
		if (analysisType.equals(BuiltInAnalysisTypes.SISTR_TYPING)) {
			Analysis analysis = submission.getAnalysis();
			Path path = analysis.getAnalysisOutputFile(sistrFileKey)
					.getFile();

			try {
				String json = new Scanner(new BufferedReader(new FileReader(path.toFile()))).useDelimiter("\\Z")
						.next();

				// verify file is proper json file and map to a SistrResult list
				ObjectMapper mapper = new ObjectMapper();
				List<SistrResult> sistrResults = mapper.readValue(json, new TypeReference<List<SistrResult>>() {
				});

				if (sistrResults.size() > 0) {
					// should only ever be one sample for these results
					if (samples != null && samples.size() == 1) {
						Sample sample = samples.iterator()
								.next();
						return new AnalysisSistrResults(sample.getSampleName(), false, sistrResults.get(0));
					} else {
						logger.error("Invalid number of associated samples for submission " + submission);
					}
				} else {
					logger.error("SISTR results for file [" + path + "] are not correctly formatted");
				}
			} catch (FileNotFoundException e) {
				logger.error("File [" + path + "] not found", e);
			} catch (JsonParseException | JsonMappingException e) {
				logger.error("Error attempting to parse file [" + path + "] as JSON", e);
			} catch (IOException e) {
				logger.error("Error reading file [" + path + "]", e);
			}
		}
		return new AnalysisSistrResults(null, true, null);
	}

	/**
	 * Delete an {@link AnalysisSubmission} by id.
	 *
	 * @param analysisSubmissionId the submission ID to delete.
	 * @param locale               Locale of the logged in user
	 * @return A message stating the submission was deleted
	 */
	@RequestMapping("/delete/{analysisSubmissionId}")
	@ResponseBody
	public Map<String, String> deleteAjaxAnalysisSubmission(@PathVariable Long analysisSubmissionId,
			final Locale locale) {
		final AnalysisSubmission deletedSubmission = analysisSubmissionService.read(analysisSubmissionId);
		analysisSubmissionService.delete(analysisSubmissionId);
		return ImmutableMap.of("result",
				messageSource.getMessage("analysis.delete.message", new Object[] { deletedSubmission.getLabel() },
						locale));
	}

	/**
	 * Download all output files from an {@link AnalysisSubmission}
	 *
	 * @param analysisSubmissionId Id for a {@link AnalysisSubmission}
	 * @param response             {@link HttpServletResponse}
	 */
	@RequestMapping(value = "/download/{analysisSubmissionId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public void getAjaxDownloadAnalysisSubmission(@PathVariable Long analysisSubmissionId,
			HttpServletResponse response) {
		AnalysisSubmission analysisSubmission = analysisSubmissionService.read(analysisSubmissionId);

		Analysis analysis = analysisSubmission.getAnalysis();
		Set<AnalysisOutputFile> files = analysis.getAnalysisOutputFiles();
		FileUtilities.createAnalysisOutputFileZippedResponse(response, analysisSubmission.getName(), files);
	}

	/**
	 * Prepare the download of multiple {@link AnalysisOutputFile} by adding them to a selection.
	 *
	 * @param outputs Info for {@link AnalysisOutputFile} to download
	 * @param response {@link HttpServletResponse}
	 * @return Map with the size of the selection for download.
	 */
	@RequestMapping(value = "/download/prepare", method = RequestMethod.POST)
	@ResponseBody
	public Map prepareDownload(@RequestBody List<ProjectSampleAnalysisOutputInfo> outputs,
			HttpServletResponse response) {
		final Long selectionSize = analysisOutputFileDownloadManager.setSelection(outputs);
		response.setStatus(HttpServletResponse.SC_CREATED);
		return ImmutableMap.of("selectionSize", selectionSize);
	}

	/**
	 * Download the selected {@link AnalysisOutputFile}.
	 *
	 * @param filename Optional filename for file download.
	 * @param response {@link HttpServletResponse}
	 */
	@RequestMapping(value = "/download/selection", produces = MediaType.APPLICATION_JSON_VALUE)
	public void downloadSelection(@RequestParam(required = false, defaultValue = "analysis-output-files-batch-download") String  filename, HttpServletResponse response) {
		Map<ProjectSampleAnalysisOutputInfo, AnalysisOutputFile> files = analysisOutputFileDownloadManager.getSelection();
		FileUtilities.createBatchAnalysisOutputFileZippedResponse(response, filename, files);
	}

	/**
	 * Download single output files from an {@link AnalysisSubmission}
	 *
	 * @param analysisSubmissionId Id for a {@link AnalysisSubmission}
	 * @param fileId               the id of the file to download
	 * @param filename             Optional filename for file download.
	 * @param response             {@link HttpServletResponse}
	 */
	@RequestMapping(value = "/download/{analysisSubmissionId}/file/{fileId}")
	public void getAjaxDownloadAnalysisSubmissionIndividualFile(@PathVariable Long analysisSubmissionId,
			@PathVariable Long fileId, @RequestParam(defaultValue = "", required = false) String filename, HttpServletResponse response) {
		AnalysisSubmission analysisSubmission = analysisSubmissionService.read(analysisSubmissionId);

		Analysis analysis = analysisSubmission.getAnalysis();
		Set<AnalysisOutputFile> files = analysis.getAnalysisOutputFiles();

		Optional<AnalysisOutputFile> optFile = files.stream()
				.filter(f -> f.getId()
						.equals(fileId))
				.findAny();
		if (!optFile.isPresent()) {
			throw new EntityNotFoundException("Could not find file with id " + fileId);
		}

		if (!Strings.isNullOrEmpty(filename)) {
			FileUtilities.createSingleFileResponse(response, optFile.get(), filename);
		} else {
			FileUtilities.createSingleFileResponse(response, optFile.get());
		}
	}

	/**
	 * Get a newick file associated with a specific {@link AnalysisSubmission}.
	 *
	 * @param submissionId {@link Long} id for an {@link AnalysisSubmission}
	 * @return {@link Map} containing the newick file contents.
	 * @throws IOException {@link IOException} if the newick file is not found
	 */
	@RequestMapping("/{submissionId}/newick")
	@ResponseBody
	public Map<String, Object> getNewickForAnalysis(@PathVariable Long submissionId) throws IOException {
		final String treeFileKey = "tree";

		AnalysisSubmission submission = analysisSubmissionService.read(submissionId);
		Analysis analysis = submission.getAnalysis();
		AnalysisOutputFile file = analysis.getAnalysisOutputFile(treeFileKey);
		List<String> lines = Files.readAllLines(file.getFile());
		return ImmutableMap.of("newick", lines.get(0));
	}

	/**
	 * Get an image file associated with a specific {@link AnalysisSubmission} by file name.
	 *
	 * @param submissionId {@link Long} id for an {@link AnalysisSubmission}
	 * @param filename     {@link String} filename for an {@link AnalysisOutputFile}
	 * @param locale       locale of the logged in user
	 * @return {@link String} containing the image file contents as a base64 encoded string.
	 */
	@RequestMapping("{submissionId}/image")
	@ResponseBody
	public ResponseEntity<String> getImageFile(@PathVariable Long submissionId, String filename, Locale locale) {
		AnalysisSubmission submission = analysisSubmissionService.read(submissionId);
		Set<AnalysisOutputFile> files = submission.getAnalysis()
				.getAnalysisOutputFiles();
		AnalysisOutputFile outputFile = null;

		try {
			for (AnalysisOutputFile file : files) {
				if (file.getFile()
						.toFile()
						.getName()
						.contains(filename)) {
					outputFile = file;
					break;
				}
			}
			return ResponseEntity.ok(Base64.getEncoder()
					.encodeToString(outputFile.getBytesForFile()));
		} catch (IOException e) {
			logger.error("Unable to open image file");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(messageSource.getMessage("AnalysisOutputs.unableToReadImageFile", null, locale));
		}
	}

	/**
	 * Get the metadata associated with a template for an analysis.
	 *
	 * @param submissionId {@link Long} identifier for the {@link AnalysisSubmission}
	 * @return {@link Map}
	 */
	@RequestMapping("/{submissionId}/metadata")
	@ResponseBody
	public Map<String, Object> getMetadataForAnalysisSamples(@PathVariable Long submissionId) {
		AnalysisSubmission submission = analysisSubmissionService.read(submissionId);
		Collection<Sample> samples = sampleService.getSamplesForAnalysisSubmission(submission);

		// Let's get a list of all the metadata available that is unique.
		Set<String> terms = new HashSet<>();
		for (Sample sample : samples) {
			if (!sample.getMetadata().isEmpty()) {
				Map<MetadataTemplateField, MetadataEntry> metadata = sample.getMetadata();
				terms.addAll(
						metadata.keySet().stream().map(MetadataTemplateField::getLabel).collect(Collectors.toSet()));
			}
		}

		// Get the metadata for the samples;
		Map<String, Object> metadata = new HashMap<>();
		for (Sample sample : samples) {
			Map<MetadataTemplateField, MetadataEntry> sampleMetadata = sample.getMetadata();
			Map<String, MetadataEntry> stringMetadata = new HashMap<>();
			sampleMetadata.entrySet().forEach(e -> {
				stringMetadata.put(e.getKey().getLabel(), e.getValue());
			});

			Map<String, MetadataEntry> valuesMap = new HashMap<>();
			for (String term : terms) {

				MetadataEntry value = stringMetadata.get(term);
				if (value == null) {
					// Not all samples will have the same metadata associated with it.  If a sample
					// is missing one of the terms, just give it an empty string.
					value = new MetadataEntry("", "text");
				}

				valuesMap.put(term, value);
			}
			metadata.put(sample.getLabel(), valuesMap);
		}

		return ImmutableMap.of(
				"terms", terms,
				"metadata", metadata
		);
	}

	/**
	 * Get a list of all {@link MetadataTemplate}s for the {@link AnalysisSubmission}
	 *
	 * @param submissionId id of the {@link AnalysisSubmission}
	 * @return a map of {@link MetadataTemplate}s
	 */
	@RequestMapping("/{submissionId}/metadata-templates")
	@ResponseBody
	public Map<String, Object> getMetadataTemplatesForAnalysis(@PathVariable Long submissionId) {
		AnalysisSubmission submission = analysisSubmissionService.read(submissionId);
		List<Project> projectsUsedInAnalysisSubmission = projectService.getProjectsUsedInAnalysisSubmission(submission);

		Set<Long> projectIds = new HashSet<>();
		Set<Map<String, Object>> templates = new HashSet<>();

		for (Project project : projectsUsedInAnalysisSubmission) {
			if (!projectIds.contains(project.getId())) {
				projectIds.add(project.getId());

				// Get the templates for the project
				List<ProjectMetadataTemplateJoin> templateList = metadataTemplateService
						.getMetadataTemplatesForProject(project);
				for (ProjectMetadataTemplateJoin projectMetadataTemplateJoin : templateList) {
					MetadataTemplate metadataTemplate = projectMetadataTemplateJoin.getObject();
					Map<String, Object> templateMap = ImmutableMap.of("label", metadataTemplate.getLabel(), "id",
							metadataTemplate.getId());
					templates.add(templateMap);
				}
			}
		}

		return ImmutableMap.of("templates", templates);
	}

	/**
	 * Generates a list of metadata fields for a five template.
	 *
	 * @param templateId {@link Long} id for the {@link MetadataTemplate} that the fields are required.
	 * @return {@link Map}
	 */
	@RequestMapping("/{submissionId}/metadata-template-fields")
	@ResponseBody
	public Map<String, Object> getMetadataTemplateFields(@RequestParam Long templateId) {
		MetadataTemplate template = metadataTemplateService.read(templateId);
		List<MetadataTemplateField> metadataFields = template.getFields();
		List<String> fields = new ArrayList<>();
		for (MetadataTemplateField metadataField : metadataFields) {
			fields.add(metadataField.getLabel());
		}
		return ImmutableMap.of("fields", fields);
	}

	/**
	 * Construct the model parameters for results with a newick output
	 * {@link Analysis}
	 *
	 * @param submissionId The analysis submission id
	 * @param locale       The users current {@link Locale}
	 * @return dto which contains the newick string and an optional message
	 * @throws IOException If the tree file couldn't be read
	 */
	@RequestMapping("/{submissionId}/tree")
	public AnalysisTreeResponse getNewickTree(@PathVariable Long submissionId, Locale locale) throws IOException {
		AnalysisSubmission submission = analysisSubmissionService.read(submissionId);

		//get the analysis output files
		Set<AnalysisOutputFile> analysisOutputFiles = submission.getAnalysis()
				.getAnalysisOutputFiles();

		//loop through the files looking for with a newick file.  Get the first one
		Optional<AnalysisOutputFile> treeOptional = analysisOutputFiles.stream()
				.filter(f -> FileUtilities.getFileExt(f.getFile()
						.toString())
						.equals(TREE_EXT))
				.findFirst();
		String tree = null;
		String message = null;

		if (!treeOptional.isPresent()) {
			logger.debug("No tree file for analysis: " + submission);
			tree=null;
			message= messageSource.getMessage("AnalysisPhylogeneticTree.noTreeFound",
					new Object[] {}, locale);
		} else {
			AnalysisOutputFile file = treeOptional.get();

			try {
				List<String> lines = Files.readAllLines(file.getFile());

				if (lines.size() > 0) {
					tree = lines.get(0);

					if (lines.size() > 1) {
						logger.warn("Multiple lines in tree file, will only display first tree. For analysis: " + submission);
						message = messageSource.getMessage("AnalysisPhylogeneticTree.multipleTrees", new Object[] {},
								locale);
					}

					if (EMPTY_TREE.equals(tree)) {
						logger.debug("Empty tree found, will hide tree preview. For analysis: " + submission);
						tree = null;
						message = messageSource.getMessage("AnalysisPhylogeneticTree.emptyTree", new Object[] {}, locale);
					}
				}
			} catch (NoSuchFileException e) {
				logger.debug("File was not found: " + e.toString());
			}
		}
		return new AnalysisTreeResponse(tree, message);
	}

	/**
	 * Parse excel file and return an ExcelData dto which
	 * contains the row data as well as the headers.
	 *
	 * @param submissionId The analysis submission id
	 * @param filename     The name of the excel file to parse
	 * @param sheetIndex   The index of the sheet in the excel workbook to parse
	 * @return dto which contains the headers and rows of the excel file
	 */
	@RequestMapping(value = "/{submissionId}/parseExcel")
	@ResponseBody
	public ExcelData parseExcelFile(@PathVariable Long submissionId, String filename, Integer sheetIndex) {
		AnalysisSubmission submission = analysisSubmissionService.read(submissionId);
		Set<AnalysisOutputFile> files = submission.getAnalysis()
				.getAnalysisOutputFiles();
		AnalysisOutputFile outputFile = null;

		for (AnalysisOutputFile file : files) {
			if (file.getFile()
					.toFile()
					.getName()
					.contains(filename)) {
				outputFile = file;
				break;
			}
		}
		// If the index of the sheet is not
		// supplied then we set it to 0
		if (sheetIndex == null) {
			sheetIndex = 0;
		}
		return FileUtilities.parseExcelFile(outputFile, sheetIndex);
	}

	/**
	 * Get the full analysis provenance
	 *
	 * @param submissionId The analysis submission id
	 * @param filename     The name of the file for which to get the provenance
	 * @return dto which contains the file provenance
	 */
	@RequestMapping(value = "/{submissionId}/provenance")
	@ResponseBody
	public AnalysisProvenanceResponse getProvenanceByFile(@PathVariable Long submissionId, String filename) {
		AnalysisSubmission submission = analysisSubmissionService.read(submissionId);
		Analysis analysis = submission.getAnalysis();
		AnalysisOutputFile outputFile = null;
		AnalysisProvenanceResponse analysisProvenance = null;
		ArrayList<AnalysisToolExecutionParameters> executionParameters;

		// get all output files produced by analysis and get the one which matches the file for which to display provenance
		Set<AnalysisOutputFile> files = analysis.getAnalysisOutputFiles();

		for (AnalysisOutputFile file : files) {
			if (file.getLabel()
					.contains(filename)) {
				outputFile = file;
				break;
			}
		}

		if (outputFile != null) {
			ToolExecution tool = outputFile.getCreatedByTool();

			// get the execution parameters for the created by tool
			executionParameters = getExecutionParameters(tool);

			analysisProvenance = new AnalysisProvenanceResponse(outputFile.getLabel(),
					new AnalysisToolExecution(tool.getLabel(), executionParameters, getPreviousExecutionTools(tool)));
		}
		return analysisProvenance;
	}

	/*
	 * Recursive function to get the previous execution tools and their parameters
	 *
	 * @param tool The tool to get the previous execution tools and their parameters for
	 * @return an arraylist of previous execution tools for the tool
	 */
	private ArrayList<AnalysisToolExecution> getPreviousExecutionTools(ToolExecution tool) {
		ArrayList<AnalysisToolExecution> previousExecutionTools = new ArrayList<>();
		ArrayList<AnalysisToolExecutionParameters> executionParameters;

		for (ToolExecution currTool : new ArrayList<>(getPrevTools(tool))) {
			executionParameters = getExecutionParameters(currTool);

			previousExecutionTools.add(new AnalysisToolExecution(currTool.getLabel(), executionParameters,
					getPreviousExecutionTools(currTool)));
		}
		return previousExecutionTools;
	}

	/*
	 * Gets the previous steps (tools) for the tool
	 *
	 * @param tool The tool to get the previous steps for
	 * @return set of previous execution tools for the tool
	 */
	private Set<ToolExecution> getPrevTools(ToolExecution tool) {
		return tool.getPreviousSteps();
	}

	/*
	 * Gets the execution parameters for the tool
	 *
	 * @param tool The tool to get the execution parameters for
	 * @return an arraylist of execution parameters for the tool
	 */
	private ArrayList<AnalysisToolExecutionParameters> getExecutionParameters(ToolExecution tool) {
		ArrayList<AnalysisToolExecutionParameters> executionParameters = new ArrayList<>();

		for (Map.Entry<String, String> entry : tool.getExecutionTimeParameters()
				.entrySet()) {
			executionParameters.add(new AnalysisToolExecutionParameters(entry.getKey(), entry.getValue()));
		}
		return executionParameters;
	}

	/**
	 * Response object storing a project and whether or not it's shared with a
	 * given {@link AnalysisSubmission}
	 */
	@SuppressWarnings("unused")
	private class SharedProjectResponse {
		private Project project;
		private boolean shared;

		public SharedProjectResponse(Project project, boolean shared) {
			this.project = project;
			this.shared = shared;
		}

		public Project getProject() {
			return project;
		}

		public boolean isShared() {
			return shared;
		}
	}

	/**
	 * UI Model to return Sequence files with its accompanying sample.
	 */
	private class SampleSequencingObject implements Comparable<SampleSequencingObject>{
		private Sample sample;
		private SequencingObject sequencingObject;

		SampleSequencingObject(SequencingObject sequencingObject) {
			this.sequencingObject = sequencingObject;
			try {
				SampleSequencingObjectJoin sampleSequencingObjectJoin = sampleService.getSampleForSequencingObject(
						sequencingObject);
				if(sampleSequencingObjectJoin != null) {
					this.sample = sampleSequencingObjectJoin.getSubject();
				}
			} catch (Exception e) {
				logger.debug("Sequence file [" + sequencingObject.getIdentifier() + "] does not have a parent sample",
						e);
				sample = null;
			}
		}

		public Long getId() {
			return sequencingObject.getId();
		}

		public Sample getSample() {
			return sample;
		}

		public SequencingObject getSequencingObject() {
			return sequencingObject;
		}

		@Override
		public int compareTo(SampleSequencingObject b) {
			if (this.sample == null && b.sample == null) {
				return 0;
			} else if (this.sample == null) {
				return -1;
			} else if (b.sample == null) {
				return 1;
			}
			return this.sample.getLabel()
					.compareTo(b.sample.getLabel());
		}
	}

}
