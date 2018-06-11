import axios from "axios";

export function putSampleInCart(projectId, sampleIds) {
  return axios.post(`${window.TL.BASE_URL}cart/add/samples`, {
    params: {
      projectId,
      sampleIds
    }
  });
}
