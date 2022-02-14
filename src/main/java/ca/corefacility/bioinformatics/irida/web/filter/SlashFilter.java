package ca.corefacility.bioinformatics.irida.web.filter;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * A simple filter that wraps {@link HttpServletRequest} to replace any incoming
 * requests with two or more slashes with a single slash. This is to band-aid a
 * bug that we encountered where multiple slashes in a URL would cause our REST
 * controllers to build links for the response that had duplicated sections,
 * resulting in invalid links.
 *
 */
@Component
public class SlashFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(SlashFilter.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		logger.info("Initializing slash filter.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		final HttpServletRequest servletRequest = (HttpServletRequest) request;
		// in test (but not with curl!), the request URI was coming through like
		// "/api%2F%2Fprojects/5/samples/1"
		// and I expect that this was also true for the remote project stuff.
		// Spring routed the URLs correctly as though they were decoded, but of
		// course failed to render valid links, so decode the URI first, then
		// check if there's doubled up slashes.
		final String decodedUri = URLDecoder.decode(servletRequest.getRequestURI(), "UTF-8");

		// if there is a context path (likely there is when running in
		// production in a servlet container like Tomcat) then you should strip
		// the context path before you do any forwarding.
		final String contextPath = servletRequest.getContextPath();
		final String contextlessUri = decodedUri.replaceFirst(contextPath, "");

		final boolean containsSlashes = contextlessUri.contains("//");
		final boolean isRequestRequest = servletRequest.getDispatcherType().equals(DispatcherType.REQUEST);

		logger.trace("Request URI is: [" + contextlessUri + "]");
		if (containsSlashes && isRequestRequest) {

			final String redirectURI = contextlessUri.replaceAll("/{2,}", "/");
			logger.trace("Handled redirect URI is: " + redirectURI);
			request.getRequestDispatcher(redirectURI).forward(request, response);
		} else {
			logger.trace("Not handling double-slash request because request URI doesn't contain double slashes: ["
					+ containsSlashes + "] or is not request request: [" + isRequestRequest + "]");
			chain.doFilter(request, response);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void destroy() {
		logger.info("Destroying slash filter.");
	}
}
