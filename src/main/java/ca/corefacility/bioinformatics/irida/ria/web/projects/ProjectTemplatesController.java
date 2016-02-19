package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by josh on 2016-02-19.
 */
@Controller
@RequestMapping("/projects/templates")
public class ProjectTemplatesController {

	@RequestMapping("/remove-modal")
	public String getRemoveSamplesFromProjectModal(@RequestParam List<String> names) {

		return "projects/templates/remove";
	}
}
