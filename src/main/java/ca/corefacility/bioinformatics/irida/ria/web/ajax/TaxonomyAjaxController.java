package ca.corefacility.bioinformatics.irida.ria.web.ajax;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.TaxonomyEntry;
import ca.corefacility.bioinformatics.irida.service.TaxonomyService;
import ca.corefacility.bioinformatics.irida.util.TreeNode;

@RestController
@RequestMapping("/ajax/taxonomy")
public class TaxonomyAjaxController {
	private final TaxonomyService taxonomyService;

	@Autowired
	public TaxonomyAjaxController(TaxonomyService taxonomyService) {
		this.taxonomyService = taxonomyService;
	}

	@RequestMapping("")
	public ResponseEntity<List<TaxonomyEntry>> searchTaxonomy(@RequestParam String q) {
		Collection<TreeNode<String>> results = taxonomyService.search(q);
		List<TaxonomyEntry> entries = results.stream().map(TaxonomyEntry::new).collect(Collectors.toList());
		return ResponseEntity.ok(entries);
	}
}
