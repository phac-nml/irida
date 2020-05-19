import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`/ajax/projects/${window.project.id}/user-groups`);

export async function removeUserGroupFromProject({ groupId }) {
  return axios
    .delete(`${BASE_URL}?groupId=${groupId}`)
    .then(({ data }) => data);
}

export async function getAvailableGroupsForProject() {}
