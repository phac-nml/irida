package ca.corefacility.bioinformatics.irida.ria.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
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

	/**
	 * This is to handle all ajax calls for html fragments that do not need to
	 * be populated with a model.
	 * 
	 * @param name
	 *            The name of the template required.
	 * @return the location of the template
	 */
	@RequestMapping(value = "/gen/{name}")
	public String getGenericFrag(@PathVariable String name) {
		logger.debug("Creating site menu.");
		return "fragments/" + name;
	}
}
