import axios from "axios";

const URL = `${window.TL.BASE_URL}ajax/projects`;

export const getPagedProjectsForUser = params =>
  axios
    .post(
      `${URL}?admin=${window.location.href.includes("all")}
`,
      params
    )
    .then(response => response.data);

export const getAssociatedProjects = id =>
  axios.get(`${URL}/${id}/settings/associated`).then(response => response.data);

export const removeAssociatedProject = (id, associatedId) =>
  axios.post(
    `${URL}/${id}/settings/associated/remove?associatedId=${associatedId}`
  );

export const getAvailableAssociatedProjects = (id, query) =>
  axios
    .get(`${URL}/${id}/settings/associated/available?query=${query}`)
    .then(response => response.data);
