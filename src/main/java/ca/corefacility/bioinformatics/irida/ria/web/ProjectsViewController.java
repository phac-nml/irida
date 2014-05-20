package ca.corefacility.bioinformatics.irida.ria.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by josh on 2014-05-20.
 */
@Controller
@RequestMapping("/projects/view")
public class ProjectsViewController {
	private static final Logger logger = LoggerFactory.getLogger(ProjectsViewController.class);

	@RequestMapping(value = "main", method = RequestMethod.GET)
	public String getProjectsView() {
		logger.debug("Creating partial for projects main view.");
		return "views/projects";
	}
}

