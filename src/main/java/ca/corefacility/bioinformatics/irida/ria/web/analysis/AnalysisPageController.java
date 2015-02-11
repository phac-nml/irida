package ca.corefacility.bioinformatics.irida.ria.web.analysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by josh on 15-02-10.
 */
@Controller
@RequestMapping("/analysis/{id}")
public class AnalysisPageController {
	private static final Logger logger = LoggerFactory.getLogger(AnalysisPageController.class);

	private static final String PAGE = "analysis/analysis_page";
	/*
	 * SERVICES
	 */

	@RequestMapping
	public String getAnalysisPage(Long analysisId) {
		return PAGE;
	}
}
