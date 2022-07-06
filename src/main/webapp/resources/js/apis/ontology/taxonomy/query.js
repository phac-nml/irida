import axios from "axios";
import { taxonomy_search_ontology_route } from "../../routes";

const TAXONOMY = "taxonomy";
const ONTOLOGY_URLS = { [TAXONOMY]: taxonomy_search_ontology_route() };

/**
 * Search an ontology for a specific term
 *
 * @param {string} query - term to search for in ontology, will do partial matching
 * @param {string} ontology - which ontology to search
 * @returns {Promise<void|AxiosResponse<any>>}
 */
export default async function searchOntology({ query, ontology }) {
  // Make sure the ontology we are searching actually exits.
  if (ONTOLOGY_URLS[ontology]) {
    return axios
      .get(`${ONTOLOGY_URLS[ontology]}?q=${query}`)
      .then(({ data }) => data);
  }

  /*
  This is purely for development, want to warn developers if the ontology they
  are searching is actually set up.
   */
  return Promise.resolve(
    console.error(`NO ONTOLOGY: ${ontology}, cannot query.`)
  );
}

export { TAXONOMY };
