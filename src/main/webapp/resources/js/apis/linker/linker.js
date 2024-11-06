import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`ajax/ngs-linker`);

export function getNGSLinkerCode({ sampleIds, projectId }) {
  return axios.post(`${BASE_URL}/cmd`, {
    sampleIds,
    projectId,
  });
}
