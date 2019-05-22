import axios from "axios";

export const getTranslations = ({ page, component }) => {
  const URL = `${window.TL.BASE_URL}${page}/translations${
    component ? `/${component}` : ""
  }`;

  const translations = sessionStorage.getItem(URL);
  if (translations) {
    return Promise.resolve(JSON.parse(translations));
  }

  return axios.get(URL).then(({ data }) => {
    sessionStorage.setItem(URL, JSON.stringify(data));
    return data;
  });
};
