import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const TAXONOMY = "taxonomy";
const ONTOLOGY_URLS = { [TAXONOMY]: setBaseUrl("ajax/taxonomy") };

export default async function searchOntology({ query, ontology }) {
  if (ONTOLOGY_URLS[ontology]) {
    return axios
      .get(`${ONTOLOGY_URLS[ontology]}?q=${query}`)
      .then(({ data }) => data);
  }
  return Promise.resolve(
    console.error(`NO ONTOLOGY: ${ontology}, cannot query.`)
  );
}

export { TAXONOMY };
