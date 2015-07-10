package ca.corefacility.bioinformatics.irida.ria.web;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dandelion.core.Context;
import com.github.dandelion.core.storage.AssetStorage;
import com.github.dandelion.core.storage.StorageEntry;
import com.github.dandelion.core.util.AssetUtils;
import com.github.dandelion.core.web.DandelionServlet;
import com.github.dandelion.core.web.WebConstants;

/**
 * Override the default {@link DandelionServlet} behaviour to allow loading
 * files from a `webapp` locator that are relative to an asset. This is
 * specifically used in the case where font-awesome loads font files as a
 * relative path in a CSS file. This **DOES NOT** use the Dandelion caching
 * system and instead just serves files directly from the filesystem.
 * 
 */
public class AssetDependencyDandelionServlet extends HttpServlet {
	private static final Logger logger = LoggerFactory.getLogger(AssetDependencyDandelionServlet.class);

	private static final Pattern JS_CSS_QUERY = Pattern.compile("(js|css)$", Pattern.CASE_INSENSITIVE);

	private final DandelionServlet delegateServlet;

	/**
	 * Create a new customized dandelion servlet that will allow loading
	 * dependencies for assets.
	 * 
	 * @param dandelionServlet
	 *            the servlet to use when we're just passing through directly to
	 *            the next servlet (for JavaScript and CSS files).
	 */
	public AssetDependencyDandelionServlet(final DandelionServlet dandelionServlet) {
		this.delegateServlet = dandelionServlet;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		if (JS_CSS_QUERY.matcher(request.getRequestURI()).find()) {
			delegateServlet.service(request, response);
			return;
		}

		logger.trace("Handling non-css/js request in customized dandelion servlet: [" + request.getRequestURI() + "]");
		final Context context = (Context) request.getAttribute(WebConstants.DANDELION_CONTEXT_ATTRIBUTE);

		// find the location of the Dandelion asset that potentially "owns" the
		// relative requested resource by getting the cache key from the
		// request, loading the storage entry from the asset storage mechanism
		// and determining the file location from the request.
		final String cacheKey = AssetUtils.extractCacheKeyFromRequest(request);
		final AssetStorage assetStorage = context.getAssetStorage();
		final StorageEntry entry = assetStorage.get(cacheKey);
		final String configLocation = entry.getAsset().getConfigLocation();
		logger.trace("Loading entry from [" + configLocation + "]");
		final String resourceLocation = request.getServletContext().getRealPath(configLocation);
		logger.trace("Loading entry from [" + resourceLocation + "]");

		Path parentPath = Paths.get(resourceLocation);
		logger.trace("Found original resource at: [ " + parentPath + "].");
		final String requestURI = request.getRequestURI();

		// get the relative path suffix of the file that we're *actually*
		// looking for (i.e., fonts/fontawesome-webfont.woff).
		final String pathSuffix = requestURI.substring(requestURI.indexOf(cacheKey) + cacheKey.length() + 1,
				requestURI.length());

		while (parentPath.getParent() != null) {
			// now start stripping off parts of the path of the "owning"
			// resource until we get to the root of the filesystem.
			final Path parent = parentPath.getParent();

			// if we found the resource, write it to the output stream in
			// the response and terminate.
			final Path possibleLocation = parent.resolve(pathSuffix);
			if (Files.exists(possibleLocation)) {
				logger.trace("Found requested resource at [" + possibleLocation + "]");
				Files.copy(possibleLocation, response.getOutputStream());
				return;
			}
			parentPath = parent;
		}

		// if we get here, we navigated the filesystem all the way to the
		// root, so we couldn't find what we were looking for.
		throw new FileNotFoundException("Could not find requested resource [" + requestURI + "]");
	}
}
