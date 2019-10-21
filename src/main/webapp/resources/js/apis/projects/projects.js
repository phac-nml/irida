import axios from "axios";

export const getPagedProjectsForUser = params =>
  axios
    .post(
      `${window.TL.BASE_URL}ajax/projects?admin=${window.location.href.includes(
        "all"
      )}
`,
      params
    )
    .then(response => response.data);
