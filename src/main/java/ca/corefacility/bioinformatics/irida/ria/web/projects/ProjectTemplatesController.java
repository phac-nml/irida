package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by josh on 2016-02-19.
 */
@Controller
@RequestMapping("/projects/templates")
public class ProjectTemplatesController {

	/**
	 * Creates the modal to remove samples from a project.
	 *
	 * @param names
	 * 		{@link List} of sample names to remove.
	 * @param model
	 * 		{@link Model}
	 *
	 * @return
	 */
	@RequestMapping("/remove-modal")
	public String getRemoveSamplesFromProjectModal(@RequestParam(name = "names[]") List<String> names, Model model) {
		model.addAttribute("names", names);
		return "projects/templates/remove-modal.tmpl";
	}
}
