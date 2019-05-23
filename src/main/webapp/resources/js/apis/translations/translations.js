import axios from "axios";

/**
 * Handle requesting translations for a page or component.
 * These are only requested the first time they are encountered during a session.
 * Once loaded the translation are cached in sessionStorage for quick fetching on
 * subsequent requests.
 * @param {string} page - the name of the page the translation are for.
 * @param {string} component - optional. Use if translations required for a specific component.
 * @returns {Promise<AxiosResponse<any>>|Promise<any> | Promise}
 */
export const getTranslations = async ({ page, component }) => {
  const URL = `${window.TL.BASE_URL}${page}/translations${
    component ? `/${component}` : ""
  }`;

  const translations = sessionStorage.getItem(URL);
  // Return the translation if it was cached into storage.
  if (translations) {
    return Promise.resolve(JSON.parse(translations));
  }

  // Fetch the translations.
  return axios.get(URL).then(({ data }) => {
    // Store into sessionStorage for faster retrieval next time.
    sessionStorage.setItem(URL, JSON.stringify(data));
    return data;
  });
};
