import axios from "axios";

const language = document.querySelector("html").getAttribute("lang");

axios
  .get(`${window.TL.BASE_URL}dist/i18n.${language}.json`)
  .then(response => console.log(response.data));

export default (i18n = key => {});
