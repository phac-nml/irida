package ca.corefacility.bioinformatics.irida.ria.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mobile.device.site.SitePreference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Controller
@RequestMapping("/projects/view")
public class ProjectsViewController {
	private static final Logger logger = LoggerFactory.getLogger(ProjectsViewController.class);

	@RequestMapping(value = "main", method = RequestMethod.GET)
	public String getProjectsView(SitePreference sitePreference) {
		logger.debug("Creating partial for projects main view.");
		if(sitePreference.isMobile()){
			return "views/404";
		}
		else {
			return "views/projects";
		}
	}
}

