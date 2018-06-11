import axios from "axios";

/**
 * Add samples for a project to the cart.
 * @param {number} projectId Identifier for the project the samples are from.
 * @param {array} sampleIds array of sample ids to add to cart.
 * @returns {AxiosPromise<any>}
 */
export function putSampleInCart(projectId, sampleIds) {
  const params = new URLSearchParams();
  params.append("projectId", projectId);
  params.append("sampleIds[]", sampleIds);

  return axios.post(`${window.TL.BASE_URL}cart/add/samples`, params);
}
