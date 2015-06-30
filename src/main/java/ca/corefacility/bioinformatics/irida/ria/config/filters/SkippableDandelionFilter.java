/*
 * [The "BSD licence"]
 * Copyright (c) 2013-2014 Dandelion
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of Dandelion nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package ca.corefacility.bioinformatics.irida.ria.config.filters;

import java.io.IOException;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dandelion.core.Context;
import com.github.dandelion.core.asset.Asset;
import com.github.dandelion.core.asset.AssetDomPosition;
import com.github.dandelion.core.asset.AssetQuery;
import com.github.dandelion.core.html.HtmlTag;
import com.github.dandelion.core.monitoring.GraphViewer;
import com.github.dandelion.core.utils.HtmlUtils;
import com.github.dandelion.core.web.ByteArrayResponseWrapper;
import com.github.dandelion.core.web.DandelionFilter;
import com.github.dandelion.core.web.WebConstants;

/**
 * Filter which extends {@link DandelionFilter}. This implementation checks a
 * request param for whether to skip the dandelion functionality BEFORE the
 * request is executed. If the flag is set, this will simply pass down the
 * filter chain.
 * 
 * @see DandelionFilter
 */
public class SkippableDandelionFilter extends DandelionFilter implements Filter {

	private static Logger LOG = LoggerFactory.getLogger(SkippableDandelionFilter.class);

	/**
	 * The Dandelion context.
	 */
	private Context context;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		LOG.info("Initializing the Dandelion context");
		context = new Context(filterConfig);
		LOG.info("Dandelion context initialized");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse serlvetResponse, FilterChain filterChain)
			throws IOException, ServletException {

		// Only filter HTTP requests
		if (!(servletRequest instanceof HttpServletRequest)) {
			LOG.warn("The AssetFilter only applies to HTTP requests");
			filterChain.doFilter(servletRequest, serlvetResponse);
			return;
		}

		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) serlvetResponse;

		request.setAttribute(WebConstants.DANDELION_CONTEXT_ATTRIBUTE, context);

		// Check if the request has the "dandelionAssetFilterState" flag set
		// before wrapping response
		if (checkFilterStateFlag(request)) {

			// Bundle reloading (development mode only)
			if (context.isDevModeEnabled() && request.getParameter(WebConstants.DANDELION_RELOAD_BUNDLES) != null) {
				LOG.info("Bundle reloading requested via request parameter");
				context.initBundleStorage();
				LOG.info("Bundle reloaded");
			}

			// Wraps the response before applying the filter chain
			ByteArrayResponseWrapper wrappedResponse = new ByteArrayResponseWrapper(response);
			filterChain.doFilter(request, wrappedResponse);

			// Bundle graph viewer display (development mode only)
			if (context.isDevModeEnabled() && request.getParameter(WebConstants.DANDELION_SHOW_GRAPH) != null) {
				GraphViewer graphViewer = new GraphViewer(context);
				response.getWriter().print(graphViewer.getView(request, response, filterChain));
				return;
			}

			byte[] bytes = wrappedResponse.toByteArray();

			if (isRelevant(request, wrappedResponse)) {

				String html = new String(bytes);

				Set<Asset> assetsHead = new AssetQuery(request, context).withPosition(AssetDomPosition.head).perform();

				if (!assetsHead.isEmpty()) {
					StringBuilder htmlHead = new StringBuilder();
					for (Asset asset : assetsHead) {
						HtmlTag tag = HtmlUtils.transformAsset(asset);
						htmlHead.append(tag.toHtml());
						htmlHead.append('\n');
					}

					html = html.replace("</head>", htmlHead + "\n</head>");
				}

				Set<Asset> assetsBody = new AssetQuery(request, context).withPosition(AssetDomPosition.body).perform();

				if (!assetsBody.isEmpty()) {
					StringBuilder htmlBody = new StringBuilder();
					for (Asset asset : assetsBody) {
						HtmlTag tag = HtmlUtils.transformAsset(asset);
						htmlBody.append(tag.toHtml());
						htmlBody.append('\n');
					}
					html = html.replace("</body>", htmlBody + "</body>");
				}

				// Modified HTML written to the response
				response.getWriter().print(html);
			} else {
				// The response is left untouched
				response.getOutputStream().write(bytes);
			}
		} else {
			// if flag is set, continue with filter chain
			filterChain.doFilter(request, response);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isRelevant(HttpServletRequest request, ByteArrayResponseWrapper wrappedResponse) {
		boolean dandelionFilterApplyable = true;

		if (wrappedResponse.getContentType() == null || !wrappedResponse.getContentType().contains("text/html")) {
			dandelionFilterApplyable = false;
		}

		if (!checkFilterStateFlag(request)) {
			dandelionFilterApplyable = false;
		}

		return dandelionFilterApplyable;
	}

	/**
	 * Checks if the request has the
	 * {@link WebConstants#DANDELION_ASSET_FILTER_STATE} flag set.
	 * 
	 * @param request
	 *            The incoming {@link HttpServletRequest}
	 * @return {@code true} unless the
	 *         {@link WebConstants#DANDELION_ASSET_FILTER_STATE} is set to true.
	 */
	public boolean checkFilterStateFlag(HttpServletRequest request) {
		boolean dandelionFilterApplyable = true;

		// Check whether the filter has been explicitely disabled
		// (possibly by other components) either from a request attribute...
		if (request.getAttribute(WebConstants.DANDELION_ASSET_FILTER_STATE) != null) {
			dandelionFilterApplyable = dandelionFilterApplyable
					&& Boolean.parseBoolean(String.valueOf(request
							.getAttribute(WebConstants.DANDELION_ASSET_FILTER_STATE)));

			if (!dandelionFilterApplyable) {
				LOG.debug("DandelionFilter explicitely disabled by the {} attribute for the request '{}'",
						WebConstants.DANDELION_ASSET_FILTER_STATE, request.getRequestURI());
			}
		}
		// ... or from a request parameter
		else if (request.getParameter(WebConstants.DANDELION_ASSET_FILTER_STATE) != null) {

			dandelionFilterApplyable = dandelionFilterApplyable
					&& Boolean.parseBoolean(String.valueOf(request
							.getParameter(WebConstants.DANDELION_ASSET_FILTER_STATE)));

			if (!dandelionFilterApplyable) {
				LOG.debug("DandelionFilter explicitely disabled by the {} parameter for the request '{}'",
						WebConstants.DANDELION_ASSET_FILTER_STATE, request.getRequestURI());
			}
		}

		return dandelionFilterApplyable;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void destroy() {
		context.destroy();
	}
}