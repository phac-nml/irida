package ca.corefacility.bioinformatics.irida.service;

import java.util.Collection;

import ca.corefacility.bioinformatics.irida.util.TreeNode;

/**
 * Service for reading taxonomy information
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public interface TaxonomyService {
	/**
	 * Search for a tree of taxonomy terms with a given search term
	 * 
	 * @param searchTerm
	 *            The term to search
	 * @return a List of {@link TreeNode}s with the requested search term
	 */
	public Collection<TreeNode<String>> search(String searchTerm);
}
