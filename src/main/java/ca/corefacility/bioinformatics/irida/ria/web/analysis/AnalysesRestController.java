package ca.corefacility.bioinformatics.irida.ria.web.analysis;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.dto.AnalysesListRequest;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.dto.AnalysesListResponse;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.dto.AnalysisModel;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;

@RestController
@RequestMapping("/ajax/analyses")
public class AnalysesRestController {

	private AnalysisSubmissionService analysisSubmissionService;

	@Autowired
	public AnalysesRestController(AnalysisSubmissionService analysisSubmissionService) {
		this.analysisSubmissionService = analysisSubmissionService;
	}

	@RequestMapping("/list")
	public AnalysesListResponse getPagedAnalyses(@RequestBody AnalysesListRequest analysesListRequest,
			@RequestParam(required = false, defaultValue = "user") String type) {

		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		User user = (User) authentication.getPrincipal();

		/*
		If they are requesting to list all submissions make sure they are truly on the administrator page
		which would throw a true error and redirect the user else where.
		 */
		if (type.equals("all") && !user.getSystemRole()
				.equals(Role.ROLE_ADMIN)) {
			type = "user";
		}

		Page<AnalysisSubmission> page;
		PageRequest pageRequest = new PageRequest(analysesListRequest.getCurrent(),
				analysesListRequest.getPageSize(), analysesListRequest.getSort());
		page = analysisSubmissionService.listAllSubmissions("", null, null, null, pageRequest);

		List<AnalysisModel> analyses = page.getContent().stream().map(AnalysisModel::new).collect(Collectors.toList());

		return new AnalysesListResponse(analyses, page.getTotalElements());
	}
}

