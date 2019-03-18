package ca.corefacility.bioinformatics.irida.service.impl.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.service.TaxonomyService;
import ca.corefacility.bioinformatics.irida.util.TreeNode;

import com.github.springtestdbunit.DbUnitTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiJdbcDataSourceConfig.class })
@ActiveProfiles("it")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExecutionListener.class })
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
