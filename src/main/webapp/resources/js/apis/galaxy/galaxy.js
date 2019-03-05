import axios from "axios";

const GALAXY_AJAX_URL = `${window.TL.BASE_URL}ajax/galaxy-export`;

/**
 * Query the server to find out if the galaxy client has a valid authentication
 * token.
 * @param {string} clientId - the identifier for the galaxy client
 * @returns {Promise<AxiosResponse<any> | never>}
 */
export const getGalaxyClientAuthentication = clientId =>
  axios
    .get(`${GALAXY_AJAX_URL}/authorized?clientId=${clientId}`)
    .then(({ data }) => data);

/**
 * Get the samples that are in the cart in correct format to be sent to galaxy.
 * @returns {Promise<AxiosResponse<any> | never>}
 */
export const getGalaxySamples = () =>
  axios.get(`${GALAXY_AJAX_URL}/samples`).then(({ data }) => data);

export const removeGalaxySession = () => axios.get(`${GALAXY_AJAX_URL}/remove`);
