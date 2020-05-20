import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`/ajax/projects/${window.project.id}/user-groups`);

export async function removeUserGroupFromProject({ groupId }) {
  return axios
    .delete(`${BASE_URL}?groupId=${groupId}`)
    .then(({ data }) => data);
}

export async function getAvailableGroupsForProject(query) {
  return axios
    .get(`${BASE_URL}/available?query=${query}`)
    .then(({ data }) => data);
}

export async function addUserGroupToProject({ groupId, role }) {
  return axios
    .post(`${BASE_URL}/add`, { role, id: groupId })
    .then(({ data }) => data);
}

export async function updateUserGroupRoleOnProject({ id, role }) {
  return axios
    .put(`${BASE_URL}/role?id=${id}&role=${role}`)
    .then(({ data }) => data);
}
