import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`ajax/users/edit`);

/**
 * Update the disabled status of a user by user id
 * @param {boolean} isEnabled - the new state of the user
 * @param {number} id - identifier for the user
 * @returns {Promise<AxiosResponse<T>>}
 */
export async function setUsersDisabledStatus({ isEnabled, id }) {
  try {
    return await axios.put(`${BASE_URL}?isEnabled=${isEnabled}&id=${id}`);
  } catch (e) {
    console.log(e);
  }
}
