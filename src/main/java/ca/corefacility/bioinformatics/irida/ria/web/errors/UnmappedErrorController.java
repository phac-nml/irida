package ca.corefacility.bioinformatics.irida.ria.web.errors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * Serves error pages that are mapped directly in web.xml.
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@Controller
public class UnmappedErrorController {
	private static final Logger logger = LoggerFactory.getLogger(UnmappedErrorController.class);

	@Value("${mail.server.email}")
	private String adminEmail;

	/**
	 * The basic 404 page.
	 * 
	 * @return
	 */
	@RequestMapping(value = "/404", produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView basic404() {
		logger.error("User requested page that does not exist");
		ModelAndView modelAndView = new ModelAndView(ExceptionHandlerController.NOT_FOUND_PAGE);
		modelAndView.addObject("adminEmail", adminEmail);
		return modelAndView;
	}

	@RequestMapping(value = "/404")
	@ResponseBody
	public String basic404Json() {
		logger.error("User requested page that does not exist via REST API");
		return "";
	}
}
