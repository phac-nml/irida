package ca.corefacility.bioinformatics.irida.ria.utilities;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.model.remote.resource.RemoteResource;

/**
 * In memory cache for storing {@link RemoteResource}s with a retrievable
 * identifier. This will specifically be used when a {@link RemoteResource}
 * object needs to be displayed in a form on a page and must be able to be
 * selected by a user.
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 * @param <Type>
 *            The type of {@link RemoteResource} to store
 */
public class RemoteObjectCache<Type extends RemoteResource> {
	private static final Logger logger = LoggerFactory.getLogger(RemoteObjectCache.class);
	private Map<Integer, Type> cache;

	public RemoteObjectCache() {
		cache = new HashMap<>();
		logger.trace("RemoteCache initialized");
	}

	public Type readResource(Integer id) {
		logger.trace("Reading id " + id);
		return cache.get(id);
	}

	public Integer addResource(Type resource) {
		logger.trace("Cache size is " + cache.size());
		int hashCode = resource.hashCode();
		if (!cache.containsKey(hashCode)) {
			logger.trace(resource + " added to cache");
			cache.put(hashCode, resource);
		} else {
			logger.trace(resource + " already in cache");
		}

		return hashCode;
	}
}
