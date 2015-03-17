package ca.corefacility.bioinformatics.irida.ria.utilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.utils.model.IdentifiableTestEntity;

public class RemoteObjectCacheTest {
	private RemoteObjectCache<IdentifiableTestEntity> cache;
	private IdentifiableTestEntity entity;
	private Integer entityId;
	private RemoteAPI api;

	@Before
	public void setUp() {
		cache = new RemoteObjectCache<>();
		entity = new IdentifiableTestEntity();
		api = new RemoteAPI();
		entityId = cache.addResource(entity, api);
	}

	@Test
	public void testAddResource() {
		IdentifiableTestEntity testEntity = new IdentifiableTestEntity();
		testEntity.setIntegerValue(5);
		Integer addResource = cache.addResource(testEntity, api);
		assertNotNull(addResource);
	}

	@Test
	public void testReadResource() {
		CacheObject<IdentifiableTestEntity> cacheObject = cache.readResource(entityId);
		IdentifiableTestEntity readResource = cacheObject.getResource();
		assertEquals(entity, readResource);
		assertEquals(api, cacheObject.getAPI());
	}

}
