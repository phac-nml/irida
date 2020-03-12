import axios from "axios";

const BASE_URL = `ajax/ngs-linker`;

export function getNGSLinkerCode({ sampleIds, projectId }) {
  return axios.post(`${BASE_URL}/cmd`, {
    sampleIds,
    projectId
  });
}
