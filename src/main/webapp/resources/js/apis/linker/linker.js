import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

export function getNGSLinkerCode({ sampleIds, projectId }) {
  return axios.post(setBaseUrl(`ajax/ngs-linker/cmd`), {
    sampleIds,
    projectId
  });
}
