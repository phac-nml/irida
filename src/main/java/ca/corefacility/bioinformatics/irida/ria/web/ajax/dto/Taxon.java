package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

import java.util.ArrayList;
import java.util.List;

import ca.corefacility.bioinformatics.irida.util.TreeNode;

public class Taxon {
	private String value;
	private String text;
	private List<Taxon> children;

	public Taxon(TreeNode<String> node) {
		this.value = node.getValue();
		this.text = node.getValue();
		this.children = new ArrayList<>();

		if (!node.getChildren().isEmpty()) {
			for (TreeNode<String> child : node.getChildren()) {
				this.children.add(new Taxon(child));
			}
		}
	}

	public String getValue() {
		return value;
	}

	public String getText() {
		return text;
	}

	public List<Taxon> getChildren() {
		return children;
	}
}
