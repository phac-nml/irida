package ca.corefacility.bioinformatics.irida.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TreeNode<ValueType> {
	private TreeNode<ValueType> parent;

	private List<TreeNode<ValueType>> children;
	private ValueType value;

	public TreeNode(ValueType value) {
		this.value = value;
		children = new ArrayList<>();
	}

	public List<TreeNode<ValueType>> getChildren() {
		return children;
	}

	public void addChild(TreeNode<ValueType> node) {
		children.add(node);
	}

	public ValueType getValue() {
		return value;
	}

	public void setValue(ValueType value) {
		this.value = value;
	}

	public void setParent(TreeNode<ValueType> parent) {
		this.parent = parent;
	}

	public TreeNode<ValueType> getParent() {
		return parent;
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TreeNode) {
			@SuppressWarnings("rawtypes")
			TreeNode other = (TreeNode) obj;
			return Objects.equals(value, other.value);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "TreeNode["+value.toString()+"]";
	}

}
