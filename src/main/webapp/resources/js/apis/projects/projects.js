import axios from "axios";

export const getPagedProjectsForUser = params =>
  axios
    .post(`${window.TL.BASE_URL}ajax/projects`, params)
    .then(response => response.data);
