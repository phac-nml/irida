package ca.corefacility.bioinformatics.irida.ria.unit.web.models;

import java.util.Date;

import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.web.models.IridaBase;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IridaBaseTest {
	private final Long id = 1L;
	private final String key = "pro-";
	private final String name = "ITEM NAME";
	private final Date createdDate = new Date(1590160849L);
	private final Date modifiedDate = new Date(1653232849L);

	@Test
	public void testIridaBase() {
		IridaBase iridaBase = new IridaBase(id, key, name, createdDate, modifiedDate);
		assertEquals(id, iridaBase.getId(), "Id should not be changed");
		assertEquals(key + id, iridaBase.getKey(), "Key should be concatenated with id");
		assertEquals(name, iridaBase.getName(), "Name should not be changed");
		assertEquals(createdDate, iridaBase.getCreatedDate(), "Created date should not be changed");
		assertEquals(modifiedDate, iridaBase.getModifiedDate(), "Modified date should not be changed");
	}
}