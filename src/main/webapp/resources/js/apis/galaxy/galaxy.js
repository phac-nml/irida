import axios from "axios";
import { authenticateOauthClient } from "../oauth/oauth";
import { setBaseUrl } from "../../utilities/url-utilities";
import { galaxy_remove_session_route, galaxy_samples_route } from "../routes";

const GALAXY_AJAX_URL = setBaseUrl(`ajax/galaxy-export`);

/**
 * This will open an new window providing the user with the ability to authenticate
 * the galaxy instance if required.
 * @returns {Promise<any>}
 */
export function validateOauthClient() {
  const redirect = `${window.PAGE.galaxyRedirect}`;
  return authenticateOauthClient(window.GALAXY.CLIENT_ID, redirect)
    .then((code) => code)
    .catch((response) => response);
}

/**
 * Get the samples that are in the cart in correct format to be sent to galaxy.
 * @returns {Promise<AxiosResponse<any> | never>}
 */
export const getGalaxySamples = () =>
  axios.get(galaxy_samples_route()).then(({ data }) => data);

/**
 * Remove galaxy from the IRIDA session.
 * @returns {Promise<AxiosResponse<any> | never>}
 */
export const removeGalaxySession = () =>
  axios.get(galaxy_remove_session_route()).then(() => {
    // Let's components depending on the galaxy session know
    // that it is no longer available
    document.body.dispatchEvent(new Event("galaxy:removal"));
  });
