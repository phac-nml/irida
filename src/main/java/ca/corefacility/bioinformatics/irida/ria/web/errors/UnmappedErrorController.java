package ca.corefacility.bioinformatics.irida.ria.web.errors;

import javax.servlet.http.HttpServletRequest;

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
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/404", produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView basic404(HttpServletRequest request) {
		/**
		 * This method will usually be requested directly by the servlet so in
		 * most cases /404 won't be the page that the user went to. We want to
		 * log the page they tried to request, but HttpServletRequest only
		 * exposes the /404 path. The jetty impl of HttpServletRequest exposes
		 * the originally requested URI in its toString() method so hopefully
		 * other servlet containers will too.
		 */
		logger.error("User requested page that does not exist: " + request);
		ModelAndView modelAndView = new ModelAndView(ExceptionHandlerController.NOT_FOUND_PAGE);
		modelAndView.addObject("adminEmail", adminEmail);
		return modelAndView;
	}

	/**
	 * 404 response for JSON requests
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/404")
	@ResponseBody
	public String basic404Json(HttpServletRequest request) {
		/**
		 * See comment on UnmappedErrorController#basic404
		 */
		logger.error("User requested page that does not exist via REST API: " + request);
		return "";
	}
}
