package ca.corefacility.bioinformatics.irida.ria.web.models.project;

import java.util.Date;

import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.web.models.IridaBase;
import ca.corefacility.bioinformatics.irida.ria.web.models.ModelKeys;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IridaBaseTest {
	@Test
	public void testIridaBase() {
		Long id = 1L;
		String name = "ITEM NAME";
		Date createdDate = new Date(1590160849L);
		Date modifiedDate = new Date(1653232849L);

		IridaBase iridaBase = new IridaBase(id, name, createdDate, modifiedDate);
		assertEquals(id, iridaBase.getId(), "Id should not be changed");
		assertEquals(ModelKeys.IridaBase.label + id, iridaBase.getKey(), "Key should be concatenated with id");
		assertEquals(name, iridaBase.getName(), "Name should not be changed");
		assertEquals(createdDate, iridaBase.getCreatedDate(), "Created date should not be changed");
		assertEquals(modifiedDate, iridaBase.getModifiedDate(), "Modified date should not be changed");
	}
}