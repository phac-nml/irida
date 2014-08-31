package ca.corefacility.bioinformatics.irida.ria.web.analysis;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.service.AnalysisService;

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

	/*
	 * SERVICES
	 */
	private AnalysisService analysisService;

	@Autowired
	public AnalysisController(AnalysisService analysisService) {
		this.analysisService = analysisService;
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

		int page = Integer.parseInt(params.get("page"));
		int count = Integer.parseInt(params.get("count"));
		String sortedBy = params.get("sortedBy");
		String sortDir = params.get("sortDir");

		// Check to see if there is a filter on the type.
		String filterType = "";
		if (params.containsKey("type")) {
			filterType = params.get("type");
		}

		// Check to see if there are date filters
		// TODO: (14-08-31 - Josh) What to do with these filters?

		// TODO: (14-08-29 - Josh) Get paged analysis

		List<Map<String, String>> analysis = new ArrayList<>();
		//		int count = Integer.parseInt(params.get("count"));
		for (int i = 0; i < count; i++) {
			Map<String, String> map = new HashMap<>();
			map.put("type", "Whole Genome Phylogenomics Pipeline " + i);
			map.put("createdDate", new Date().toString());
			analysis.add(map);
		}
		result.put("analysis", analysis);
		result.put("totalAnalysis", Math.floor(Math.random() * 100));
		result.put("totalPages", Math.floor(Math.random() * 10));
		return result;
	}
}
