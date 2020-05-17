package ca.corefacility.bioinformatics.irida.ria.unit.web.ajax.ontologies;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.TaxonomyEntry;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.ontologies.TaxonomyAjaxController;
import ca.corefacility.bioinformatics.irida.service.TaxonomyService;

import com.google.common.collect.ImmutableList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class TaxonomyAjaxControllerTest {
	private TaxonomyService taxonomyService;
	private TaxonomyAjaxController controller;

	@Before
	public void setUp() {
		taxonomyService = mock(TaxonomyService.class);
		controller = new TaxonomyAjaxController(taxonomyService);

		when(taxonomyService.search(anyString())).thenReturn(ImmutableList.of());
	}

	@Test
	public void testSearchTaxonomy() {
		final String query = "esch";
		ResponseEntity<List<TaxonomyEntry>> response = controller.searchTaxonomy(query);
		verify(taxonomyService, times(1)).search(query);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
}
