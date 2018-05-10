import axios from "axios";

export function fetchTemplates(projectId) {
  return axios({
    method: "get",
    url: `${window.TL.BASE_URL}linelist/templates?projectId=${projectId}`
  });
}
