package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

import java.util.List;
import java.util.stream.Collectors;

import ca.corefacility.bioinformatics.irida.util.TreeNode;

/**
 * Used by the UI to generate a select input based on a search within the taxonomy.
 */
public class TaxonomyEntry {
	private String value;
	private String text;
	private List<TaxonomyEntry> children;

	public TaxonomyEntry(TreeNode<String> node) {
		this.value = node.getValue();
		this.text = node.getValue();
		this.children = node.getChildren().stream().map(TaxonomyEntry::new).collect(Collectors.toList());
	}

	public String getValue() {
		return value;
	}

	public String getText() {
		return text;
	}

	public List<TaxonomyEntry> getChildren() {
		return children;
	}
}
