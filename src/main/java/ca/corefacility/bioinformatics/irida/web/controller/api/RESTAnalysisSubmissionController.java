package ca.corefacility.bioinformatics.irida.web.controller.api;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResponseResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.RootResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.samples.RESTSampleSequenceFilesController;

import com.google.common.collect.ImmutableMap;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * REST controller to manage sharing of {@link AnalysisSubmission},
 * {@link Analysis}, and {@link AnalysisOutputFile} classes.
 */
@Controller
@RequestMapping(value = "/api/analysisSubmissions")
public class RESTAnalysisSubmissionController extends RESTGenericController<AnalysisSubmission> {
	private AnalysisSubmissionService analysisSubmissionService;
	private SampleService sampleService;
	private SequencingObjectService sequencingObjectService;
	private IridaWorkflowsService iridaWorkflowsService;

	// rel for reading the analysis for a submission
	public static final String ANALYSIS_REL = "analysis";

	public static final String FILE_REL = "outputFile";

	public static final String SUBMISSIONS_REL = "analysisSubmissions";

	// rels for reading input files for a submission
	public static final String INPUT_FILES_UNPAIRED_REL = "input/unpaired";
	public static final String INPUT_FILES_PAIRED_REL = "input/paired";

	// available analysis types to filter for
	public static Map<String, AnalysisType> ANALYSIS_TYPES = ImmutableMap.<String, AnalysisType>builder()
			.put("phylogenomics", BuiltInAnalysisTypes.PHYLOGENOMICS)
			.put("assembly", BuiltInAnalysisTypes.ASSEMBLY_ANNOTATION)
			.put("assembly-collection", BuiltInAnalysisTypes.ASSEMBLY_ANNOTATION_COLLECTION)
			.put("sistr", BuiltInAnalysisTypes.SISTR_TYPING)
			.build();

	@Autowired
	public RESTAnalysisSubmissionController(AnalysisSubmissionService analysisSubmissionService,
			SampleService sampleService, SequencingObjectService sequencingObjectService,
			IridaWorkflowsService iridaWorkflowsService) {
		super(analysisSubmissionService, AnalysisSubmission.class);
		this.analysisSubmissionService = analysisSubmissionService;
		this.sampleService = sampleService;
		this.sequencingObjectService = sequencingObjectService;
		this.iridaWorkflowsService = iridaWorkflowsService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Operation(operationId = "listAllAnalysisSubmissions", summary = "Lists all analysis submissions", description = "Lists all analysis submissions.", tags = "analysisSubmissions")
	@Override
	public ResponseResource<ResourceCollection<AnalysisSubmission>> listAllResources() {
		return super.listAllResources();
	}

	/**
	 * {@inheritDoc}
	 */
	@Operation(operationId = "getAnalysisSubmission", summary = "Find an analysis submission", description = "Get the analysis submission given the identifier.", tags = "analysisSubmissions")
	@Override
	public ResponseResource<AnalysisSubmission> getResource(@PathVariable Long identifier) {
		return super.getResource(identifier);
	}

	/**
	 * {@inheritDoc}
	 */
	@Operation(operationId = "createAnalysisSubmission", summary = "Create a new analysis submission", description = "Create a new analysis submission.", tags = "analysisSubmissions")
	@Override
	public ResponseResource<AnalysisSubmission> create(@RequestBody AnalysisSubmission resource,
			HttpServletResponse response) {
		return super.create(resource, response);
	}

	/**
	 * {@inheritDoc}
	 */
	@Operation(operationId = "deleteAnalysisSubmission", summary = "Delete an analysis submission", description = "Delete an analysis submission given the identifier.", tags = "analysisSubmissions")
	@Override
	public ResponseResource<RootResource> delete(@PathVariable Long identifier) {
		return super.delete(identifier);
	}

	/**
	 * {@inheritDoc}
	 */
	@Operation(operationId = "updateAnalysisSubmission", summary = "Update an analysis submission", description = "Update an analysis submission", tags = "analysisSubmissions")
	@Override
	public ResponseResource<RootResource> update(@PathVariable Long identifier,
			@RequestBody Map<String, Object> representation) {
		return super.update(identifier, representation);
	}

	/**
	 * Get all analyses of a given type
	 *
	 * @param type The type to request
	 * @return A list of {@link AnalysisSubmission}s
	 */
	@Operation(operationId = "listOfType", summary = "Find the analysis submissions by type", description = "Get all the analysis submission of a given type.", tags = "analysisSubmissions")
	@RequestMapping(value = "/analysisType/{type}", method = RequestMethod.GET)
	public ResponseResource<ResourceCollection<AnalysisSubmission>> listOfType(@PathVariable String type) {
		if (!ANALYSIS_TYPES.containsKey(type)) {
			throw new EntityNotFoundException("Analysis type not found");
		}
		AnalysisType analysisType = ANALYSIS_TYPES.get(type);
		Set<UUID> workflowIds;
		try {
			workflowIds = iridaWorkflowsService.getAllWorkflowsByType(analysisType)
					.stream()
					.map(IridaWorkflow::getWorkflowDescription)
					.map(IridaWorkflowDescription::getId)
					.collect(Collectors.toSet());
		} catch (IridaWorkflowNotFoundException e) {
			throw new EntityNotFoundException("Analysis type not found", e);
		}

		List<AnalysisSubmission> analysesOfType = analysisSubmissionService.getAnalysisSubmissionsAccessibleByCurrentUserByWorkflowIds(
				workflowIds);

		ResourceCollection<AnalysisSubmission> resourceCollection = new ResourceCollection<>(analysesOfType.size());
		for (AnalysisSubmission s : analysesOfType) {
			s.add(constructCustomResourceLinks(s));
			s.add(linkTo(methodOn(RESTAnalysisSubmissionController.class).getResource(s.getId())).withSelfRel());
			resourceCollection.add(s);
		}

		resourceCollection.add(linkTo(methodOn(RESTAnalysisSubmissionController.class).listOfType(type)).withSelfRel());
		resourceCollection.add(linkTo(RESTAnalysisSubmissionController.class).withRel(SUBMISSIONS_REL));
		ResponseResource<ResourceCollection<AnalysisSubmission>> responseObject = new ResponseResource<>(
				resourceCollection);

		return responseObject;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Collection<org.springframework.hateoas.Link> constructCollectionResourceLinks(
			ResourceCollection<AnalysisSubmission> list) {
		Collection<org.springframework.hateoas.Link> links = super.constructCollectionResourceLinks(list);

		for (String type : ANALYSIS_TYPES.keySet()) {
			links.add((org.springframework.hateoas.Link) linkTo(
					methodOn(RESTAnalysisSubmissionController.class).listOfType(type)).withRel(
					SUBMISSIONS_REL + "/" + type));
		}

		return links;
	}

	/**
	 * Get the {@link SequenceFilePair}s used for the {@link AnalysisSubmission}
	 *
	 * @param identifier {@link AnalysisSubmission} id
	 * @return list of {@link SequenceFilePair}s
	 */
	@Operation(operationId = "getAnalysisInputFilePairs", summary = "Find the sequence file pairs of an analysis submission", description = "Get all the sequence file pairs used for the given analysis submission.", tags = "analysisSubmissions")
	@RequestMapping(value = "/{identifier}/sequenceFiles/pairs", method = RequestMethod.GET)
	public ResponseResource<ResourceCollection<SequenceFilePair>> getAnalysisInputFilePairs(
			@PathVariable Long identifier) {
		AnalysisSubmission analysisSubmission = analysisSubmissionService.read(identifier);

		Set<SequenceFilePair> pairs = sequencingObjectService.getSequencingObjectsOfTypeForAnalysisSubmission(
				analysisSubmission, SequenceFilePair.class);
		ResourceCollection<SequenceFilePair> resources = new ResourceCollection<>(pairs.size());
		for (SequenceFilePair pair : pairs) {
			SampleSequencingObjectJoin join = sampleService.getSampleForSequencingObject(pair);

			if (join != null) {
				Long sampleId = join.getSubject()
						.getId();

				pair = RESTSampleSequenceFilesController.addSequencingObjectLinks(pair, sampleId);

				resources.add(pair);
			}
		}

		resources.add(linkTo(methodOn(RESTAnalysisSubmissionController.class).getAnalysisInputFilePairs(
				identifier)).withSelfRel());
		ResponseResource<ResourceCollection<SequenceFilePair>> responseObject = new ResponseResource<>(resources);

		return responseObject;
	}

	/**
	 * get the {@link SequenceFile}s not in {@link SequenceFilePair}s used for
	 * the {@link AnalysisSubmission}
	 *
	 * @param identifier the {@link AnalysisSubmission} id
	 * @return list of {@link SequenceFile}s
	 */
	@Operation(operationId = "getAnalysisInputUnpairedFiles", summary = "Find the sequence files not in the sequence file pairs used for an analysis submission", description = "Get all the sequence files not in the sequence file pairs used for the given analysis submission.", tags = "analysisSubmissions")
	@RequestMapping(value = "/{identifier}/sequenceFiles/unpaired", method = RequestMethod.GET)
	public ResponseResource<ResourceCollection<SequencingObject>> getAnalysisInputUnpairedFiles(
			@PathVariable Long identifier) {
		AnalysisSubmission analysisSubmission = analysisSubmissionService.read(identifier);

		Set<SingleEndSequenceFile> inputFilesSingleEnd = sequencingObjectService.getSequencingObjectsOfTypeForAnalysisSubmission(
				analysisSubmission, SingleEndSequenceFile.class);
		ResourceCollection<SequencingObject> resources = new ResourceCollection<>(inputFilesSingleEnd.size());
		for (SingleEndSequenceFile file : inputFilesSingleEnd) {
			SampleSequencingObjectJoin join = sampleService.getSampleForSequencingObject(file);

			if (join != null) {
				SequencingObject sequencingObject = join.getObject();

				RESTSampleSequenceFilesController.addSequencingObjectLinks(sequencingObject, join.getSubject()
						.getId());

				resources.add(sequencingObject);
			}
		}

		resources.add(linkTo(methodOn(RESTAnalysisSubmissionController.class).getAnalysisInputUnpairedFiles(
				identifier)).withSelfRel());
		ResponseResource<ResourceCollection<SequencingObject>> responseObject = new ResponseResource<>(resources);

		return responseObject;
	}

	/**
	 * Get the {@link Analysis} for an {@link AnalysisSubmission}.
	 *
	 * @param identifier {@link AnalysisSubmission} identifier to read
	 * @return An {@link Analysis}
	 */
	@Operation(operationId = "getAnalysisForSubmission", summary = "Find the analysis of an analysis submission", description = "Get the analysis for the given analysis submission.", tags = "analysisSubmissions")
	@RequestMapping(value = "/{identifier}/analysis", method = RequestMethod.GET)
	public ResponseResource<Analysis> getAnalysisForSubmission(@PathVariable Long identifier) {
		AnalysisSubmission read = analysisSubmissionService.read(identifier);

		if (read.getAnalysisState() != AnalysisState.COMPLETED) {
			throw new EntityNotFoundException("Analysis is not completed");
		}

		Analysis analysis = read.getAnalysis();

		analysis.add(linkTo(methodOn(RESTAnalysisSubmissionController.class).getAnalysisForSubmission(
				identifier)).withSelfRel());

		/*
		 * Add links to the available files
		 */
		for (Map.Entry<String, AnalysisOutputFile> entry : analysis.getAnalysisOutputFilesMap()
				.entrySet()) {
			analysis.add(linkTo(methodOn(RESTAnalysisSubmissionController.class).getAnalysisOutputFile(identifier,
					entry.getValue()
							.getId())).withRel(FILE_REL + "/" + entry.getKey()));
		}

		ResponseResource<Analysis> responseObject = new ResponseResource<>(analysis);

		return responseObject;
	}

	/**
	 * Get an analysis output file for a given submission
	 *
	 * @param submissionId The {@link AnalysisSubmission} id
	 * @param fileId       The {@link AnalysisOutputFile} id
	 * @return An {@link AnalysisOutputFile}
	 */
	@Operation(operationId = "getAnalysisOutputFile", summary = "Find the analysis output file of an analysis submission", description = "Get the analysis output file for the given analysis submission.", tags = "analysisSubmissions")
	@RequestMapping(value = "/{submissionId}/analysis/file/{fileId}", method = RequestMethod.GET)
	public ResponseResource<AnalysisOutputFile> getAnalysisOutputFile(@PathVariable Long submissionId,
			@PathVariable Long fileId) {

		AnalysisOutputFile analysisOutputFile = getOutputFileForSubmission(submissionId, fileId);
		analysisOutputFile.add(
				linkTo(methodOn(RESTAnalysisSubmissionController.class).getAnalysisOutputFile(submissionId,
						analysisOutputFile.getId())).withSelfRel());

		ResponseResource<AnalysisOutputFile> responseObject = new ResponseResource<>(analysisOutputFile);

		return responseObject;
	}

	/**
	 * Get the {@link AnalysisOutputFile} for an {@link AnalysisSubmission} and given file ID
	 *
	 * @param submissionId the ID of the {@link AnalysisSubmission}
	 * @param fileId       the ID of the {@link AnalysisOutputFile}
	 * @return the {@link AnalysisOutputFile} if available
	 */
	private AnalysisOutputFile getOutputFileForSubmission(Long submissionId, Long fileId) {
		AnalysisSubmission read = analysisSubmissionService.read(submissionId);

		if (read.getAnalysisState() != AnalysisState.COMPLETED) {
			throw new EntityNotFoundException("Analysis is not completed");
		}

		Optional<AnalysisOutputFile> outputFileOpt = read.getAnalysis()
				.getAnalysisOutputFiles()
				.stream()
				.filter(f -> f.getId()
						.equals(fileId))
				.findFirst();

		if (!outputFileOpt.isPresent()) {
			throw new EntityNotFoundException("No output file with this id " + fileId);
		}

		return outputFileOpt.get();
	}

	/**
	 * {@inheritDoc} add analysis rel if available
	 */
	@Override
	protected Collection<org.springframework.hateoas.Link> constructCustomResourceLinks(AnalysisSubmission resource) {
		Collection<org.springframework.hateoas.Link> links = new HashSet<>();
		if (resource.getAnalysisState()
				.equals(AnalysisState.COMPLETED)) {
			links.add(linkTo(methodOn(RESTAnalysisSubmissionController.class).getAnalysisForSubmission(
					resource.getId())).withRel(ANALYSIS_REL));
		}

		links.add(linkTo(methodOn(RESTAnalysisSubmissionController.class).getAnalysisInputUnpairedFiles(
				resource.getId())).withRel(INPUT_FILES_UNPAIRED_REL));

		links.add(linkTo(methodOn(RESTAnalysisSubmissionController.class).getAnalysisInputFilePairs(
				resource.getId())).withRel(INPUT_FILES_PAIRED_REL));

		return links;
	}

}
