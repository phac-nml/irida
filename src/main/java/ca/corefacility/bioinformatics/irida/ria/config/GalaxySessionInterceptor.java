package ca.corefacility.bioinformatics.irida.ria.config;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Interceptor to determine if the {@link HttpServletRequest} is from a Galaxy Instance.
 * If it is this gets added to the session so that the user is presented with the appropriate
 * information, and cart page.
 */
public class GalaxySessionInterceptor extends HandlerInterceptorAdapter {
	// HTTP session variable name for Galaxy callback variable
	public static final String GALAXY_CALLBACK_URL = "galaxyCallbackUrl";
	public static final String GALAXY_CLIENT_ID = "galaxyClientID";

	@Autowired
	private HttpSession session;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		Map<String, String[]> requestMap = request.getParameterMap();

		boolean galaxyRequest = requestMap.containsKey(GALAXY_CALLBACK_URL) && requestMap.containsKey(GALAXY_CLIENT_ID);
		boolean alreadySeen = session.getAttribute(GALAXY_CALLBACK_URL) != null;
		if (galaxyRequest && !alreadySeen) {
			session.setAttribute(GALAXY_CALLBACK_URL, requestMap.get(GALAXY_CALLBACK_URL)[0]);
			session.setAttribute(GALAXY_CLIENT_ID, requestMap.get(GALAXY_CLIENT_ID)[0]);
			response.sendRedirect(request.getRequestURI());
		}

		return super.preHandle(request, response, handler);
	}
}
