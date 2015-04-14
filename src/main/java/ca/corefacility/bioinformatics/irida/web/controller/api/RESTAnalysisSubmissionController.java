package ca.corefacility.bioinformatics.irida.web.controller.api;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.IridaResourceSupport;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisAssemblyAnnotation;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;

import com.google.common.collect.ImmutableMap;

@Controller
@RequestMapping(value = "/api/analysisSubmission")
public class RESTAnalysisSubmissionController extends RESTGenericController<AnalysisSubmission> {
	private AnalysisSubmissionService analysisSubmissionService;
	public static final String ANALYSIS_LINK = "analysis";

	public static Map<String, Class<? extends Analysis>> ANALYSIS_TYPES = ImmutableMap.of("phylogenomics",
			AnalysisPhylogenomicsPipeline.class, "assembly", AnalysisAssemblyAnnotation.class);

	@Autowired
	public RESTAnalysisSubmissionController(AnalysisSubmissionService analysisSubmissionService) {
		super(analysisSubmissionService, AnalysisSubmission.class);
		this.analysisSubmissionService = analysisSubmissionService;
	}

	/**
	 * Get all analyses of a given type
	 * 
	 * @param type
	 *            The type to request
	 * @return ModelMap containing the requested type of resource
	 */
	@RequestMapping("/analysisType/{type}")
	public ModelMap listOfType(@PathVariable String type) {
		ModelMap model = new ModelMap();

		if (!ANALYSIS_TYPES.containsKey(type)) {
			throw new EntityNotFoundException("Analysis type not found");
		}
		Class<? extends Analysis> analysisClass = ANALYSIS_TYPES.get(type);

		Iterable<AnalysisSubmission> submissions = analysisSubmissionService.findAll();

		List<AnalysisSubmission> analysesOfType = StreamSupport.stream(submissions.spliterator(), false)
				.filter((s) -> s.getAnalysis() != null && s.getAnalysis().getClass().equals(analysisClass))
				.collect(Collectors.toList());

		ResourceCollection<AnalysisSubmission> resourceCollection = new ResourceCollection<>(analysesOfType.size());
		for (AnalysisSubmission s : analysesOfType) {
			s.add(constructCustomResourceLinks(s));
			s.add(linkTo(methodOn(RESTAnalysisSubmissionController.class).getResource(s.getId())).withSelfRel());
			resourceCollection.add(s);
		}

		resourceCollection.add(linkTo(methodOn(RESTAnalysisSubmissionController.class).listOfType(type)).withSelfRel());
		model.addAttribute(RESOURCE_NAME, resourceCollection);

		return model;

	}

	/**
	 * {@inheritDoc} Adding links to analysis types in resource collection
	 */
	@Override
	public ModelMap listAllResources() {
		ModelMap listAllResources = super.listAllResources();
		IridaResourceSupport object = (IridaResourceSupport) listAllResources.get(RESOURCE_NAME);
		for (String type : ANALYSIS_TYPES.keySet()) {
			object.add(linkTo(methodOn(RESTAnalysisSubmissionController.class).listOfType(type)).withRel(type));
		}

		return listAllResources;
	}

	@RequestMapping("/{identifier}/analysis")
	public ModelMap getAnalysisForSubmission(@PathVariable Long identifier) {
		ModelMap model = new ModelMap();
		AnalysisSubmission read = analysisSubmissionService.read(identifier);

		if (read.getAnalysisState() != AnalysisState.COMPLETED) {
			throw new EntityNotFoundException("Analysis is not completed");
		}

		Analysis analysis = read.getAnalysis();

		/*
		 * Add links to the available files
		 */
		for (String name : analysis.getAnalysisOutputFileNames()) {
			analysis.add(linkTo(
					methodOn(RESTAnalysisSubmissionController.class).getAnalysisOutputFile(identifier, name)).withRel(
					name));
		}

		model.addAttribute(RESOURCE_NAME, analysis);

		return model;
	}

	@RequestMapping("/{submissionId}/analysis/file/{fileId}")
	public ModelMap getAnalysisOutputFile(@PathVariable Long submissionId, @PathVariable String fileId) {
		ModelMap model = new ModelMap();
		AnalysisSubmission read = analysisSubmissionService.read(submissionId);

		if (read.getAnalysisState() != AnalysisState.COMPLETED) {
			throw new EntityNotFoundException("Analysis is not completed");
		}

		AnalysisOutputFile analysisOutputFile = read.getAnalysis().getAnalysisOutputFile(fileId);

		model.addAttribute(RESOURCE_NAME, analysisOutputFile);

		return model;
	}

	@Override
	protected Collection<Link> constructCustomResourceLinks(AnalysisSubmission resource) {
		Collection<Link> links = new HashSet<>();
		if (resource.getAnalysisState().equals(AnalysisState.COMPLETED)) {
			links.add(linkTo(
					methodOn(RESTAnalysisSubmissionController.class).getAnalysisForSubmission(resource.getId()))
					.withRel(ANALYSIS_LINK));
		}

		return links;
	}
}
