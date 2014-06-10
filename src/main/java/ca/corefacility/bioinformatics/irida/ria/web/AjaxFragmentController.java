package ca.corefacility.bioinformatics.irida.ria.web;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This class is to handle ajax fragment requests from the wet-boew framework.
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Controller
@RequestMapping(value = "/fragments")
public class AjaxFragmentController {
	private static final Logger logger = LoggerFactory.getLogger(AjaxFragmentController.class);

	@RequestMapping(value = "/sitemenu")
	public String getSiteMenu(Model model, Principal principal) {
		String name = principal.getName();
		model.addAttribute("name", name);
		return "fragments/sitemenu";
	}
}
