import axios from "axios";

const BASE_URL = `/ajax/activities`;

export function getProjectActivities({ projectId, size = 10 }) {
  return axios
    .get(`${BASE_URL}/project?projectId=${projectId}&size=${size}`)
    .then((response) => response.data.activityList);
}
