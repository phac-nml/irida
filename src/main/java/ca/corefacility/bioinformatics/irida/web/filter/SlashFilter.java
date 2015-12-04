package ca.corefacility.bioinformatics.irida.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple filter that wraps {@link HttpServletRequest} to replace any incoming
 * requests with two or more slashes with a single slash. This is to band-aid a
 * bug that we encountered where multiple slashes in a URL would cause our REST
 * controllers to build links for the response that had duplicated sections,
 * resulting in invalid links.
 *
 */
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
		chain.doFilter(new SlashReplacingHttpServletRequestWrapper((HttpServletRequest) request), response);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void destroy() {
		logger.info("Destroying slash filter.");
	}

	/**
	 * An {@link HttpServletRequestWrapper} that replaces two or more slashes in
	 * the request URI or servlet path with single slashes.
	 *
	 */
	private static class SlashReplacingHttpServletRequestWrapper extends HttpServletRequestWrapper {

		private final String cachedRequestURI;
		private final String cachedServletPath;

		/**
		 * {@inheritDoc}
		 */
		public SlashReplacingHttpServletRequestWrapper(HttpServletRequest request) {
			super(request);

			this.cachedRequestURI = request.getRequestURI().replaceAll("/{2,}", "/");
			this.cachedServletPath = request.getServletPath().replaceAll("/{2,}", "/");
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getRequestURI() {
			return cachedRequestURI;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getServletPath() {
			return cachedServletPath;
		}
	}
}
