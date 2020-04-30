import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`/ajax/groups`);

export async function deleteUserGroup(id) {
  return axios.delete(`${BASE_URL}?id=${id}`).then(({ data }) => data);
}
