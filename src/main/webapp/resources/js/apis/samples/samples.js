import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const URL = setBaseUrl(`samples`);

export const getSampleDetails = id =>
  axios.get(`${URL}?id=${id}`).then(response => response.data);
