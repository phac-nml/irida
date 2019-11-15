package ca.corefacility.bioinformatics.irida.ria.config;

import org.springframework.web.servlet.ModelAndView;

/**
 * Functions
 */
public class InterceptorUtilities {

	/**
	 * Check to ensure that the {@link ModelAndView} exists and is not in a redirect
	 *
	 * @param modelAndView
	 * 		{@link ModelAndView}
	 *
	 * @return true if the {@link ModelAndView} is good for breadcrumbs
	 */
	protected static boolean hasGoodModelAndView(ModelAndView modelAndView) {
		return modelAndView != null && !modelAndView.getViewName().contains("redirect:");
	}
}
