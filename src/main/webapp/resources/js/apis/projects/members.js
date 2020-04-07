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

export async function getAvailableUsersForProject(query) {
  return await axios
    .get(`${BASE}/available?query=${query}`)
    .then(({ data }) => console.log(data));
}
