import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE = setBaseUrl(`/ajax/projects/${window.project.id}/groups`);

export async function updateGroupsRoleOnProject({ id, role }) {
  return await axios
    .put(`${BASE}/role?id=${id}&role=${role}`)
    .then(({ data }) => data);
}
