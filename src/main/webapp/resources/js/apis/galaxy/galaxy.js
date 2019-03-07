import axios from "axios";

const GALAXY_AJAX_URL = `${window.TL.BASE_URL}ajax/galaxy-export`;

/**
 * Get the samples that are in the cart in correct format to be sent to galaxy.
 * @returns {Promise<AxiosResponse<any> | never>}
 */
export const getGalaxySamples = () =>
  axios.get(`${GALAXY_AJAX_URL}/samples`).then(({ data }) => data);
