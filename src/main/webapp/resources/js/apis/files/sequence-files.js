import { setBaseUrl } from "../../utilities/url-utilities";
import axios from "axios";

const BASE_URL = setBaseUrl(`/ajax/sequenceFiles`);

export async function getFastQCDetails(sequencingObjectId, sequenceFileId) {
  try {
    const { data } = await axios.get(`${BASE_URL}/getFastQCDetails`, {
      params: {
        sequencingObjectId,
        sequenceFileId
      }
    });
    console.log(data);
    return data;
  }  catch (error) {
    return { error };
  }
}