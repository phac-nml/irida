import axios from "axios";
import { authenticateOauthClient } from "../oauth/oauth";

const GALAXY_AJAX_URL = `${window.TL.BASE_URL}ajax/galaxy-export`;

/**
 * This will open an new window providing the user with the ability to authenticate
 * the galaxy instance if required.
 * @returns {Promise<any>}
 */
export function validateOauthClient() {
  const redirect = `${window.TL.BASE_URL}galaxy/auth_code`;
  return authenticateOauthClient(window.GALAXY.CLIENT_ID, redirect)
    .then(code => code)
    .catch(response => response);
}

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
