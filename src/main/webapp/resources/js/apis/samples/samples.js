import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const URL = setBaseUrl(`ajax/samples`);

export const getSampleDetails = async (id) => {
  try {
    const { data } = await axios.get(`${URL}/${id}/details`);
    return data;
  } catch (e) {
    return Promise.reject(e.response.data.error);
  }
};
