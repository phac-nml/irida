import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

export async function fetchCurrentUserDetails() {
  try {
    const { data } = await axios.get(setBaseUrl("/ajax/users/current"));
    return Promise.resolve(data);
  } catch (e) {
    return Promise.reject(e.response.data);
  }
}
