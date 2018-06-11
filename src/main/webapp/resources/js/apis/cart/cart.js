import axios from "axios";

export function putSampleInCart(projectId, sampleIds) {
  const params = new URLSearchParams();
  params.append("projectId", projectId);
  params.append("sampleIds[]", sampleIds);

  return axios.post(`${window.TL.BASE_URL}cart/add/samples`, params);
}
