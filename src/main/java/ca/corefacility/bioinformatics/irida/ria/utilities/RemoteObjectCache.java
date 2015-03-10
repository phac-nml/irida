package ca.corefacility.bioinformatics.irida.ria.utilities;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RemoteResource;

/**
 * In memory cache for storing {@link RemoteResource}s with a retrievable
 * identifier. This will specifically be used when a {@link RemoteResource}
 * object needs to be displayed in a form on a page and must be able to be
 * selected by a user.
 * 
 *
 * @param <Type>
 *            The type of {@link RemoteResource} to store
 */
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RemoteObjectCache<Type extends RemoteResource> {
	private static final Logger logger = LoggerFactory.getLogger(RemoteObjectCache.class);

	private Map<Integer, CacheObject<Type>> objectCache;

	public RemoteObjectCache() {
		objectCache = new HashMap<>();
		logger.trace("RemoteCache initialized");
	}

	/**
	 * Read a resource from the cache with its given id
	 * 
	 * @param id
	 *            the resource id
	 * @return The {@link CacheObject} if found
	 * @throws EntityNotFoundException
	 *             if the object doesn't exist in the cache
	 */
	public CacheObject<Type> readResource(Integer id) throws EntityNotFoundException {
		logger.trace("Reading id " + id);
		CacheObject<Type> cacheObject = objectCache.get(id);
		if (cacheObject == null) {
			throw new EntityNotFoundException("Object id " + id + " is not in the cache.");
		}
		return cacheObject;
	}

	/**
	 * Add a resource to the cache and get an identifier for the object
	 * 
	 * @param resource
	 *            The {@link RemoteResource} to add
	 * @param api
	 *            The {@link RemoteAPI} it was read from
	 * @return the Integer hash value to access the object
	 */
	public Integer addResource(Type resource, RemoteAPI api) {
		logger.trace("Cache size is " + objectCache.size());
		int hashCode = resource.hashCode();
		if (!objectCache.containsKey(hashCode)) {
			logger.trace(resource + " added to cache");

			objectCache.put(hashCode, new CacheObject<>(resource, api));
		} else {
			logger.trace(resource + " already in cache");
		}

		return hashCode;
	}
}
