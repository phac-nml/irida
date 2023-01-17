package ca.corefacility.bioinformatics.irida.util;

import java.util.*;

/**
 * Simple generic tree node with links to parent and a list of children
 * 
 * @param <ValueType> The value to be stored by this node
 */
public class TreeNode<ValueType> {
	// parent node of this node
	private TreeNode<ValueType> parent;

	// All the childen of the node. May be empty
	private Set<TreeNode<ValueType>> children;

	// The value to be stored by this node
	private ValueType value;

	Map<String, Object> properties;

	/**
	 * Create a new {@link TreeNode} with a given value
	 * 
	 * @param value The value to set
	 */
	public TreeNode(ValueType value) {
		this.value = value;
		children = new HashSet<>();
		properties = new HashMap<>();
	}

	/**
	 * Get a list of all the children of this node
	 * 
	 * @return A List of child nodes.
	 */
	public Collection<TreeNode<ValueType>> getChildren() {
		return children;
	}

	/**
	 * Add a child to the children list
	 * 
	 * @param node The child to add
	 */
	public void addChild(TreeNode<ValueType> node) {
		children.add(node);
	}

	/**
	 * Get the value stored by this node
	 * 
	 * @return the value stored by this node
	 */
	public ValueType getValue() {
		return value;
	}

	/**
	 * Set the value for this node
	 * 
	 * @param value the value for this node
	 */
	public void setValue(ValueType value) {
		this.value = value;
	}

	/**
	 * Set the parent node
	 * 
	 * @param parent the parent node
	 */
	public void setParent(TreeNode<ValueType> parent) {
		this.parent = parent;
	}

	/**
	 * Get the parent node
	 * 
	 * @return the parent node
	 */
	public TreeNode<ValueType> getParent() {
		return parent;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	/**
	 * Add a property to the tree node
	 * 
	 * @param key   key for the property
	 * @param value value of the property
	 */
	public void addProperty(String key, Object value) {
		properties.put(key, value);
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
		return "TreeNode[" + value.toString() + "]";
	}

}
