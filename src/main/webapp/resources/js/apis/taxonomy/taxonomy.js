import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const TAXONOMY_URL = setBaseUrl("ajax/taxonomy");

export async function searchTaxonomy(query) {
  return axios.get(`${TAXONOMY_URL}?q=${query}`).then(({ data }) => data);
}
