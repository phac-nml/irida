import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE = setBaseUrl(`ajax/announcements`);

export function createNewAnnouncement({ message }) {
  return axios.post(`${BASE}/create`, { message });
}

export function updateAnnouncement({ id, message }) {
  return axios.put(`${BASE}/update`, { id, message });
}

export function deleteAnnouncement({ id }) {
  return axios({
    method: "delete",
    url: `${BASE}/delete`,
    data: {
      id
    }
  });
}
