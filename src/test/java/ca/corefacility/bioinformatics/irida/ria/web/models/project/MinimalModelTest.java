package ca.corefacility.bioinformatics.irida.ria.web.models.project;

import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.web.models.MinimalModel;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MinimalModelTest {

	@Test
	public void testMinimalModel() {
		Long id = 1L;
		String name = "ITEM NAME";
		String key = "pro-";
		MinimalItem item = new MinimalItem(id, name, key);
		assertEquals(id, item.getId(), "Id should not be changed");
		assertEquals(key + id, item.getKey(), "Key should be concatenated with id");
		assertEquals(name, item.getName(), "Name should not be changed");
	}

	static class MinimalItem extends MinimalModel {
		public MinimalItem(Long id, String name, String key) {
			super(id, name, key);
		}
	}
}