package ca.corefacility.bioinformatics.irida.ria.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class TranslationInterceptor extends HandlerInterceptorAdapter {

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		super.postHandle(request, response, handler, modelAndView);

		if (InterceptorUtilities.hasGoodModelAndView(modelAndView)) {
			// The actual html file should be the same name as the entry point.
			String view = modelAndView.getViewName().trim().split(".html")[0];

			/*
			Check to see if the file exists, if it does add the name to the model so it can be loaded onto the page.
			 */
			if (ResourceUtils.getFile("file:src/main/webapp/dist/i18n/" + view + ".html")
					.exists()) {
				modelAndView.getModel()
						.put("translations", view);
			}
		}
	}
}
