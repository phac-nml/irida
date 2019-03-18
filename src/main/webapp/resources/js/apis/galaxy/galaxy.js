import axios from "axios";

const GALAXY_AJAX_URL = `${window.TL.BASE_URL}ajax/galaxy-export`;

/**
 * Get the samples that are in the cart in correct format to be sent to galaxy.
 * @returns {Promise<AxiosResponse<any> | never>}
 */
export const getGalaxySamples = () =>
  axios.get(`${GALAXY_AJAX_URL}/samples`).then(({ data }) => data);

/**
 * Remove galaxy from the IRIDA session.
 * @returns {Promise<AxiosResponse<any> | never>}
 */
export const removeGalaxySession = () =>
  axios.get(`${GALAXY_AJAX_URL}/remove`).then(() => {
    // Let's components depending on the galaxy session know
    // that it is no longer available
    document.body.dispatchEvent(new Event("galaxy:removal"));
  });
