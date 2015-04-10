package ca.corefacility.bioinformatics.irida.web.controller.api;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Controller
@RequestMapping(value = "/api/analysisSubmission")
public class RESTAnalysisSubmissionController extends RESTGenericController<AnalysisSubmission> {
	private AnalysisSubmissionService analysisSubmissionService;
	public static final String ANALYSIS_LINK = "analysis";

	@Autowired
	public RESTAnalysisSubmissionController(AnalysisSubmissionService analysisSubmissionService) {
		super(analysisSubmissionService, AnalysisSubmission.class);
		this.analysisSubmissionService = analysisSubmissionService;
	}

	@RequestMapping("/{identifier}/analysis")
	public ModelMap getAnalysisForSubmission(@PathVariable Long identifier) {
		ModelMap model = new ModelMap();
		AnalysisSubmission read = analysisSubmissionService.read(identifier);

		if (read.getAnalysisState() != AnalysisState.COMPLETED) {
			throw new EntityNotFoundException("Analysis is not completed");
		}

		Analysis analysis = read.getAnalysis();
		model.addAttribute(RESOURCE_NAME, analysis);

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
