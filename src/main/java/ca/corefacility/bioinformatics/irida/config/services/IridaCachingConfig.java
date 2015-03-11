package ca.corefacility.bioinformatics.irida.config.services;

import net.sf.ehcache.config.CacheConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
 *
 */
@Configuration
@EnableCaching
public class IridaCachingConfig {
	private static final Logger logger = LoggerFactory.getLogger(IridaCachingConfig.class);

	@Value("${remote.sample.cache_size}")
	private Integer sampleCacheSize;

	@Value("${remote.sample.cache_expiry}")
	private Integer sampleCacheExpiry;

	/**
	 * Create the EhCache manager
	 * 
	 * @return
	 */
	@Bean
	public net.sf.ehcache.CacheManager ehCacheManager() {
		net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();

		// remote samples cache
		CacheConfiguration cacheConfiguration = new CacheConfiguration(SampleRemoteRepositoryImpl.SAMPLES_CACHE_NAME,
				sampleCacheSize);
		cacheConfiguration.setTimeToIdleSeconds(sampleCacheExpiry);
		config.addCache(cacheConfiguration);

		return net.sf.ehcache.CacheManager.create(config);
	}

	/**
	 * Create the Spring cache manager
	 * 
	 * @return
	 */
	@Bean
	@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
	public CacheManager cacheManager() {
		logger.trace("Initializing cache manager");
		EhCacheCacheManager ehCacheCacheManager = new EhCacheCacheManager(ehCacheManager());
		return ehCacheCacheManager;
	}
}
