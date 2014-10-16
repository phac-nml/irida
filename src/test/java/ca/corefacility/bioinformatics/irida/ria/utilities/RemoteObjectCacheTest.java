package ca.corefacility.bioinformatics.irida.ria.utilities;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.utils.model.RemoteIdentifiableTestEntity;

public class RemoteObjectCacheTest {
	private RemoteObjectCache<RemoteIdentifiableTestEntity> cache;
	private RemoteIdentifiableTestEntity entity;
	private Integer entityId;

	@Before
	public void setUp() {
		cache = new RemoteObjectCache<>();
		entity = new RemoteIdentifiableTestEntity();
		entityId = cache.addResource(entity);
	}

	@Test
	public void testAddResource() {
		RemoteIdentifiableTestEntity testEntity = new RemoteIdentifiableTestEntity();
		testEntity.setIntegerValue(5);
		Integer addResource = cache.addResource(testEntity);
		assertNotNull(addResource);
	}

	@Test
	public void testReadResource() {
		RemoteIdentifiableTestEntity readResource = cache.readResource(entityId);
		assertEquals(entity, readResource);
	}

}
