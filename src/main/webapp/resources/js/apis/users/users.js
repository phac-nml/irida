import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`ajax/users/edit`);

export async function setUsersDisabledStatus({ isEnabled, id }) {
  try {
    return await axios.put(`${BASE_URL}?isEnabled=${isEnabled}&id=${id}`);
  } catch (e) {
    console.log(e);
  }
}
