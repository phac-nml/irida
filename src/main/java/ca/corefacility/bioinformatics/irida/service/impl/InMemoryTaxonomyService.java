package ca.corefacility.bioinformatics.irida.service.impl;

import ca.corefacility.bioinformatics.irida.service.TaxonomyService;
import ca.corefacility.bioinformatics.irida.util.TreeNode;
import com.google.common.base.Strings;
import org.apache.jena.atlas.lib.StrUtils;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.RDFS;
import org.apache.lucene.queryparser.classic.QueryParser;

import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;


/**
 * A {@link TaxonomyService} leveraging Apache Jena's in memory storage service
 * and Apache Lucene's text searching abilities.
 */
public class InMemoryTaxonomyService implements TaxonomyService {
	private Model model;
	private Dataset dataset;

	// query to select all resources that are a subclass of the specified
	// parent that have the given word in their label
	private final static String LABEL_SEARCH_QUERY = StrUtils.strjoinNL(
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>", "SELECT * ",
			"WHERE { ?s rdfs:subClassOf* ?parent;", "rdfs:label ?label.", "FILTER regex(?label, ?term, 'i')", "}");

	private final String ROOT_IRI = "http://purl.obolibrary.org/obo/NCBITaxon_2";

	public InMemoryTaxonomyService(Path taxonomyFileLocation) {
		dataset = DatasetFactory.create();

		model = dataset.getDefaultModel();
		RDFDataMgr.read(model, taxonomyFileLocation.toString());

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<TreeNode<String>> search(String searchTerm) {
		HashMap<String, TreeNode<String>> visited = new HashMap<>();

		Set<TreeNode<String>> visitedRoots = new HashSet<>();

		if (!Strings.isNullOrEmpty(searchTerm)) {
			ParameterizedSparqlString query = new ParameterizedSparqlString(LABEL_SEARCH_QUERY);

			query.setLiteral("term", QueryParser.escape(searchTerm));
			query.setIri("parent", ROOT_IRI);
			Query q = query.asQuery();
			QueryExecution qexec = QueryExecutionFactory.create(q, dataset);
			ResultSet result = qexec.execSelect();

			while (result.hasNext()) {
				QuerySolution next = result.next();

				buildTrimmedResultTree(next.getResource("s"), searchTerm, visited);
			}

			// get all the roots
			for (Entry<String, TreeNode<String>> entry : visited.entrySet()) {
				TreeNode<String> current = entry.getValue();
				while (current.getParent() != null) {
					current = current.getParent();
				}

				if (!visitedRoots.contains(current)) {
					visitedRoots.add(current);
				}
			}
		}

		return visitedRoots;
	}

	/**
	 * Build a result tree from a searched resource. This search will look
	 * upwards in the tree until there are no more parent nodes.
	 *
	 * @param resource
	 *            The resource to start from
	 * @param searchTerm
	 *            The search term that must be included
	 * @param visited
	 *            A map of previously visited nodes.
	 * @return
	 */
	private TreeNode<String> buildTrimmedResultTree(Resource resource, String searchTerm,
			Map<String, TreeNode<String>> visited) {
		TreeNode<String> treeNode;
		String resourceURI = resource.getURI();

		if (visited.containsKey(resourceURI)) {
			treeNode = visited.get(resourceURI);
		} else {
			String elementName = resource.getProperty(RDFS.label).getObject().asLiteral().getString();
			treeNode = new TreeNode<>(elementName);
			visited.put(resourceURI, treeNode);

			Resource matchingParent = getMatchingParent(resource, searchTerm);
			if (matchingParent != null) {
				TreeNode<String> parent = buildTrimmedResultTree(matchingParent, searchTerm, visited);
				parent.addChild(treeNode);
				treeNode.setParent(parent);
			}
		}

		return treeNode;
	}

	/**
	 * Get a parent node with the matching search term
	 *
	 * @param resource
	 *            The resource to start walking up from
	 * @param searchTerm
	 *            The search term required
	 * @return A parent of the given node with the given search term in the
	 *         label.
	 */
	private Resource getMatchingParent(Resource resource, String searchTerm) {
		NodeIterator subClasses = model.listObjectsOfProperty(resource, RDFS.subClassOf);

		if (subClasses.hasNext()) {
			Resource parentNode = subClasses.next().asResource();
			String elementName = parentNode.getProperty(RDFS.label).getObject().asLiteral().getString();

			if (elementName.toLowerCase().contains(searchTerm.toLowerCase())) {
				return parentNode;
			} else {
				return getMatchingParent(parentNode, searchTerm);
			}
		}

		return null;

	}
}
