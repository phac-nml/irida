import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

export async function createProject(details) {
  try {
    const { data } = await axios.post(
      setBaseUrl(`/ajax/projects/new`),
      details
    );
    return data;
  } catch (e) {
    return Promise.reject(e.response.data.message);
  }
}
