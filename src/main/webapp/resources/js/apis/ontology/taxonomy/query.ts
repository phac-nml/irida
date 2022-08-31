import axios from "axios";
import { setBaseUrl } from "../../../utilities/url-utilities";

const TAXONOMY = "taxonomy";
const ONTOLOGY_URLS: OntologyDictionary = {
  [TAXONOMY]: setBaseUrl("ajax/taxonomy"),
};

export interface OntologyDictionary {
  [key: string]: string;
}

/**
 * Search an ontology for a specific term
 *
 * @param {string} query - term to search for in ontology, will do partial matching
 * @param {string} ontology - which ontology to search
 * @returns {Promise<void|AxiosResponse<any>>}
 */
export default async function searchOntology({
  query,
  ontology,
}: {
  query: string;
  ontology: string;
}) {
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
