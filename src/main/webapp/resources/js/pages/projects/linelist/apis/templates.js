import axios from "axios";

export function fetchTemplates(projectId) {
  return axios({
    method: "get",
    url: `${window.TL.BASE_URL}linelist/templates?projectId=${projectId}`
  });
}

export function fetchTemplate(templateId) {
  return axios({
    method: "get",
    url: `${window.TL.BASE_URL}linelist/template?templateId=${templateId}`
  });
}
