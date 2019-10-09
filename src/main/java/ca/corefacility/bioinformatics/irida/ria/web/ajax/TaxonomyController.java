package ca.corefacility.bioinformatics.irida.ria.web.ajax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.Taxon;
import ca.corefacility.bioinformatics.irida.service.TaxonomyService;
import ca.corefacility.bioinformatics.irida.util.TreeNode;

@RestController
@RequestMapping("/ajax/taxonomy")
public class TaxonomyController {
	private TaxonomyService taxonomyService;

	@Autowired
	public TaxonomyController(TaxonomyService taxonomyService) {
		this.taxonomyService = taxonomyService;
	}

	@RequestMapping()
	public List<Taxon> search(@RequestParam String term) {
		Collection<TreeNode<String>> search = taxonomyService.search(term);
		List<Taxon> taxonomy = new ArrayList<>();

		// Always return the search node if it is not already there.
		TreeNode<String> searchTermNode = new TreeNode<>(term);
		if (!search.contains(searchTermNode)) {
			taxonomy.add(new Taxon(searchTermNode));
		}

		// Add true search results
		for (TreeNode<String> node : search) {
			taxonomy.add(new Taxon(node));
		}

		return taxonomy;
	}
}
