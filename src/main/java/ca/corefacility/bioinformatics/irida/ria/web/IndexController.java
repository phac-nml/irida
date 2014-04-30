package ca.corefacility.bioinformatics.irida.ria.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Stand in class to ensure that the web app is running properly.
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Controller
public class IndexController {
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String showIndex(final Model model) {
		model.addAttribute("irida", "Hello IRIDA!");
		return "index";
	}
}
