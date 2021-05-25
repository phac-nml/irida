import axios from "axios";

const BASE_URL = `/ajax/activities`;

export function getProjectActivities({ projectId, page = 0 }) {
  return axios
    .get(`${BASE_URL}/project?projectId=${projectId}&page=${page}`)
    .then((response) => response.data.activityList);
}
