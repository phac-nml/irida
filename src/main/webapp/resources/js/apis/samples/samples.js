import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";
import { notification } from "antd";

const URL = setBaseUrl(`ajax/samples`);

export const fetchSampleDetails = async (id) => {
  try {
    const { data } = await axios.get(`${URL}/${id}/details`);
    return data;
  } catch (e) {
    return Promise.reject(e.response.data.error);
  }
};

export async function fetchSampleFiles({ sampleId, projectId }) {
  try {
    const response = await axios(
      `${URL}/${sampleId}/files${projectId ? `?projectId=${projectId}` : null}`
    );
    return response.data;
  } catch (e) {
    notification.error({ message: e.response.data.message });
  }
}
