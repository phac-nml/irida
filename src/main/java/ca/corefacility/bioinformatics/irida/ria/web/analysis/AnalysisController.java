package ca.corefacility.bioinformatics.irida.ria.web.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.Formatter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.specification.AnalysisSubmissionSpecification;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;

import com.google.common.base.Strings;

/**
 * Controller for Analysis.
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Controller
@RequestMapping("/analysis")
public class AnalysisController {
	private static final Logger logger = LoggerFactory.getLogger(AnalysisController.class);
	/*
	 * CONSTANTS
	 */

	// PAGES
	private static final String BASE = "analysis/";
	public static final String PAGE_ADMIN_ANALYSIS = BASE + "admin";

	// URI's
	private static final String URI_PAGE_ADMIN = "/admin";
	private static final String URI_AJAX_LIST_ALL_ANALYSIS = "/ajax/all";

	private Formatter<Date> dateFormatter;

	/*
	 * SERVICES
	 */
	private AnalysisSubmissionService analysisSubmissionService;

	@Autowired
	public AnalysisController(AnalysisSubmissionService analysisSubmissionService, MessageSource messageSource) {
		this.analysisSubmissionService = analysisSubmissionService;
		dateFormatter = new DateFormatter(
				messageSource.getMessage("locale.date.long", null, LocaleContextHolder.getLocale()));
	}

	// ************************************************************************************************
	// PAGES
	// ************************************************************************************************

	/**
	 * Mapping for the
	 * @return
	 */
	@RequestMapping(URI_PAGE_ADMIN)
	public String getPageAdminAnalysis() {
		logger.debug("Showing the Analysis Admin Page");
		// TODO: (14-08-29 - Josh) Once individuals can own an analysis this needs to be only admin.
		return PAGE_ADMIN_ANALYSIS;
	}

	// ************************************************************************************************
	// AJAX
	// ************************************************************************************************

	@RequestMapping(value = URI_AJAX_LIST_ALL_ANALYSIS, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getAjaxListAllAnalysis(
			@RequestParam Integer page,
			@RequestParam Integer count,
			@RequestParam String sortedBy,
			@RequestParam String sortDir,
			@RequestParam(required = false) String state,
			@RequestParam(value = "name", required = false) String nameFilter,
			@RequestParam(value = "minDate", required = false) @DateTimeFormat(
					iso = DateTimeFormat.ISO.DATE_TIME) Date minDateFilter,
			@RequestParam(value = "maxDate", required = false) @DateTimeFormat(
					iso = DateTimeFormat.ISO.DATE_TIME) Date maxDateFilter)
			throws IOException {
		Map<String, Object> result = new HashMap<>();

		// -1 because paging starts at 0, UI at 1
		page -= 1;
		Sort.Direction order = sortDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

		// Let's see if we need to filter the state
		AnalysisState stateFilter = null;
		if (!Strings.isNullOrEmpty(state)) {
			StateFilter filter = new ObjectMapper().readValue(state, StateFilter.class);
			stateFilter = filter.getState();
		}

		Specification<AnalysisSubmission> specification = AnalysisSubmissionSpecification
				.searchAnalysis(nameFilter, stateFilter, minDateFilter, maxDateFilter);
		Page<AnalysisSubmission> analysisPage = analysisSubmissionService
				.search(specification, page, count, order, sortedBy);

		List<Map<String, String>> analysisList = new ArrayList<>();
		for (AnalysisSubmission analysisSubmission : analysisPage.getContent()) {
			Map<String, String> map = new HashMap<>();
			map.put("id", analysisSubmission.getId().toString());
			map.put("name", analysisSubmission.getName());
			map.put("status", analysisSubmission.getAnalysisState().toString());
			map.put("createdDate", analysisSubmission.getCreatedDate().toString());
			map.put("createdDateString", dateFormatter.print(analysisSubmission.getCreatedDate(),
					LocaleContextHolder.getLocale()));
			analysisList.add(map);
		}

		result.put("analysis", analysisList);
		result.put("totalAnalysis", analysisPage.getTotalElements());
		result.put("totalPages", analysisPage.getTotalPages());
		return result;
	}
}
