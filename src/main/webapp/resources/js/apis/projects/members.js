import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE = setBaseUrl(`/ajax/projects/${window.project.id}/members`);

export async function removeUserFromProject(id) {
  return await axios.delete(`${BASE}?id=${id}`).then(({ data }) => data);
}

export async function updateUserRoleOnProject({ id, role }) {
  return await axios
    .put(`${BASE}/role?id=${id}&role=${role}`)
    .then(({ data }) => data);
}
