package ca.corefacility.bioinformatics.irida.service.impl.integration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ca.corefacility.bioinformatics.irida.annotation.ServiceIntegrationTest;
import ca.corefacility.bioinformatics.irida.service.TaxonomyService;
import ca.corefacility.bioinformatics.irida.util.TreeNode;

@ServiceIntegrationTest
public class InMemoryTaxonomyServiceIT {
	private static final Logger logger = LoggerFactory.getLogger(InMemoryTaxonomyServiceIT.class);

	@Autowired
	TaxonomyService taxonomyService;

	@Test
	public void testSearch() {
		String searchTerm = "sal";
		Collection<TreeNode<String>> search = taxonomyService.search(searchTerm);
		assertNotNull(search);
		assertFalse(search.isEmpty());
		for (TreeNode<String> node : search) {
			testTreeHasString(node, searchTerm);
		}
	}

	@Test
	public void testSearchEmpty() {
		String searchTerm = "";
		Collection<TreeNode<String>> search = taxonomyService.search(searchTerm);
		assertNotNull(search);
		assertTrue(search.isEmpty());
	}

	private void testTreeHasString(TreeNode<String> node, String searchTerm) {
		assertTrue(node.getValue().toLowerCase().contains(searchTerm.toLowerCase()));
		for (TreeNode<String> child : node.getChildren()) {
			logger.trace("Visited " + child);
			testTreeHasString(child, searchTerm);
		}
	}
}
