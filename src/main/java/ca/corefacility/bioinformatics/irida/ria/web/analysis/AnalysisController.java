package ca.corefacility.bioinformatics.irida.ria.web.analysis;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.format.Formatter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;

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
	private static final String URI_AJAX_LIST_ALL_ANALYSIS_TYPES = "/ajax/types";

	private Formatter<Date> dateFormatter;
	private MessageSource messageSource;

	/*
	 * SERVICES
	 */
	private AnalysisSubmissionService analysisSubmissionService;

	@Autowired
	public AnalysisController(AnalysisSubmissionService analysisSubmissionService, MessageSource messageSource) {
		this.analysisSubmissionService = analysisSubmissionService;
		this.messageSource = messageSource;

		dateFormatter = new DateFormatter(
				messageSource.getMessage("locale.date.long", null, LocaleContextHolder.getLocale()));
	}

	// ************************************************************************************************
	// PAGES
	// ************************************************************************************************

	@RequestMapping(URI_PAGE_ADMIN)
	public String getPageAdminAnalysis() {
		// TODO: (14-08-29 - Josh) Once individuals can own an analysis this needs to be only admin.
		return PAGE_ADMIN_ANALYSIS;
	}

	// ************************************************************************************************
	// AJAX
	// ************************************************************************************************

	@RequestMapping(value = URI_AJAX_LIST_ALL_ANALYSIS_TYPES, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Map<String, Object>> getAjaxListAllAnalysisTypes() {
		List<Map<String, Object>> result = new ArrayList<>();
		// TODO: (14-08-31 - Josh) Get this dynamically once flushed out by Aaron
		Map<String, Object> map = new HashMap<>();
		map.put("id", 1L);
		map.put("name", "Whole Genome Phylogenomics Pipeline");
		result.add(map);
		return result;
	}

	@RequestMapping(value = URI_AJAX_LIST_ALL_ANALYSIS, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getAjaxListAllAnalysis(@RequestParam Map<String, String> params,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date minDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date maxDate) {
		Map<String, Object> result = new HashMap<>();

		int page = Integer.parseInt(params.get("page")) - 1;
		int size = Integer.parseInt(params.get("count"));
		String sortProperty = params.get("sortedBy");
		Sort.Direction order = params.get("sortDir").equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

		// Check to see if there is a filter on the type.
//		String filterType = "";
//		if (params.containsKey("type")) {
//			filterType = params.get("type");
//		}

		// Check to see if there are date filters
		// TODO: (14-08-31 - Josh) What to do with these filters?

		// TODO: (14-08-29 - Josh) Get paged analysis
		Page<AnalysisSubmission> analysisPage = analysisSubmissionService.list(page, size, order, sortProperty);
		List<Map<String, String>> analysisList = new ArrayList<>();
		for (AnalysisSubmission analysisSubmission : analysisPage.getContent()) {
			Analysis analysis = analysisSubmission.getAnalysis();
			Map<String, String> map = new HashMap<>();
			map.put("id", analysisSubmission.getId().toString());
			map.put("type", analysis.getExecutionManagerAnalysisId());
			map.put("status", analysisSubmission.getAnalysisState().toString());
			map.put("createdDate", dateFormatter.print(analysisSubmission.getCreatedDate(),
					LocaleContextHolder.getLocale()));
			analysisList.add(map);
		}

		result.put("analysis", analysisList);
		result.put("totalAnalysis", analysisPage.getTotalElements());
		result.put("totalPages", analysisPage.getTotalPages());
		return result;
	}
}
