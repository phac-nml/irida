import axios from "axios";
import { linker_cmd_route } from "../routes";

export function getNGSLinkerCode({ sampleIds, projectId }) {
  return axios.post(linker_cmd_route(), {
    sampleIds,
    projectId,
  });
}
