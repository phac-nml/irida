package ca.corefacility.bioinformatics.irida.config;

import net.sf.ehcache.config.CacheConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import ca.corefacility.bioinformatics.irida.repositories.remote.impl.SampleRemoteRepositoryImpl;

/**
 * Configuration file for setting up EhCache caching for IRIDA
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@Configuration
@EnableCaching
public class IridaCachingConfig {
	private static final Logger logger = LoggerFactory.getLogger(IridaCachingConfig.class);

	@Bean
	public net.sf.ehcache.CacheManager ehCacheManager() {
		net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();

		// remote samples cache
		CacheConfiguration cacheConfiguration = new CacheConfiguration(SampleRemoteRepositoryImpl.SAMPLES_CACHE_NAME,
				1000);
		cacheConfiguration.setTimeToIdleSeconds(SampleRemoteRepositoryImpl.SAMPLES_CACHE_EXPIRY);
		config.addCache(cacheConfiguration);

		net.sf.ehcache.CacheManager cacheManager = net.sf.ehcache.CacheManager.create(config);
		return cacheManager;
	}

	@Bean
	@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
	public CacheManager cacheManager() {
		logger.trace("Initializing cache manager");
		EhCacheCacheManager ehCacheCacheManager = new EhCacheCacheManager(ehCacheManager());
		return ehCacheCacheManager;
	}
}
