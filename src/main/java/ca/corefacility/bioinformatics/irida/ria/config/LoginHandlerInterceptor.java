package ca.corefacility.bioinformatics.irida.ria.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.google.common.base.Strings;

public class LoginHandlerInterceptor extends HandlerInterceptorAdapter {

	private final String customText;
	private final String DARK_LOGO;
	private final String LIGHT_LOGO;

	public LoginHandlerInterceptor(String dark, String light, String customText) {
		this.customText = customText;
		this.DARK_LOGO = dark;
		this.LIGHT_LOGO = light;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		super.postHandle(request, response, handler, modelAndView);

		// ensure the request isn't for the rest api
		String servletPath = request.getServletPath();
		if (!servletPath
				.startsWith("/api") && modelAndView != null && !modelAndView.getViewName()
				.startsWith("redirect")) {
			modelAndView.getModelMap()
					.addAttribute("DARK_LOGO", DARK_LOGO);
			modelAndView.getModelMap()
					.addAttribute("LIGHT_LOGO", LIGHT_LOGO);

			if (!Strings.isNullOrEmpty(customText) && servletPath
					.contains("login")) {

				/*
				Need to add any custom html the institution want on the login page.
				 */
				modelAndView.getModelMap()
						.addAttribute("custom", customText);
			}
		}
	}
}
