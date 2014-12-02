package ca.corefacility.bioinformatics.irida.ria.web.errors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class BasicErrorPage {
	private static final Logger logger = LoggerFactory.getLogger(BasicErrorPage.class);

	@Value("${mail.server.email}")
	private String adminEmail;

	@RequestMapping("/404")
	public ModelAndView basic404() {
		logger.error("User requested page that does not exist");
		ModelAndView modelAndView = new ModelAndView(ExceptionHandlerController.NOT_FOUND_PAGE);
		modelAndView.addObject("adminEmail", adminEmail);
		return modelAndView;
	}
}
