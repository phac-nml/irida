import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE = setBaseUrl(`/ajax/projects/${window.project.id}/members`);

export async function removeUserFromProject(id) {
  return await axios.delete(`${BASE}?id=${id}`).then(({ data }) => data);
}
