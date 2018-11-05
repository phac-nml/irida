import axios from "axios";

const url = `${window.TL.BASE_URL}cart`;

/**
 * Add samples for a project to the cart.
 * @param {number} projectId Identifier for the project the samples are from.
 * @param {array} samples array of sample {ids } to add to cart.
 * @returns {AxiosPromise<any>}
 */
export const putSampleInCart = async (projectId, samples) =>
  axios.put(url, {
    projectId,
    samples
  });
