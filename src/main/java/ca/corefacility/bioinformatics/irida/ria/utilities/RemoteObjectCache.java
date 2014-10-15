package ca.corefacility.bioinformatics.irida.ria.utilities;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.model.remote.resource.RemoteResource;

public class RemoteObjectCache<Type extends RemoteResource> {
	private static final Logger logger = LoggerFactory.getLogger(RemoteObjectCache.class);
	Map<Integer, Type> cache;

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
