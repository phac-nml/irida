import axios from "axios";

let projectId;
export function fetchTemplates(id) {
  projectId = id;
  return axios({
    method: "get",
    url: `${window.TL.BASE_URL}linelist/templates?projectId=${projectId}`
  });
}

export function saveTemplate(data) {
  return axios({
    method: "post",
    url: `${window.TL.BASE_URL}linelist/templates?projectId=${projectId}`,
    data
  });
}
