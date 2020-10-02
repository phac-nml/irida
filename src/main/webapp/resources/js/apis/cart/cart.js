import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const AJAX_URL = setBaseUrl(`/ajax/cart`);

/**
 * Add samples for a project to the cart.
 * @param {number} projectId Identifier for the project the samples are from.
 * @param {array} samples array of sample {ids } to add to cart.
 * @returns {Promise<{count: any}>}
 */
export const putSampleInCart = async (projectId, samples) =>
  axios
    .post(AJAX_URL, {
      projectId,
      sampleIds: samples.map((s) => s.id),
    })
    .then(({ data }) => data);

/**
 * Get the current number of samples in the cart
 * @returns {Promise<{count: any}>}
 */
export const getCartCount = async () => {
  return axios.get(`${AJAX_URL}/count`).then(({ data }) => ({ count: data }));
};

/**
 * Get the current state of the cart.
 * @returns {Promise<void | never>}
 */
export const getCart = async () =>
  axios.get(`${AJAX_URL}`).then((response) => response.data);

export const getCartIds = async () =>
  axios.get(`${AJAX_URL}/ids`).then((response) => ({ ids: response.data }));

export const getSamplesForProjects = async (ids) =>
  axios
    .get(`${AJAX_URL}/samples?${ids.map((id) => `ids=${id}`).join("&")}`)
    .then(({ data }) => data);

/**
 * Remove all samples from the cart
 */
export const emptyCart = async () => axios.delete(`${AJAX_URL}`);

/**
 * Remove an individual sample from the cart.
 * @param {number} projectId - Identifier for a project
 * @param {number} sampleId - Identifier for a sample
 * @returns {Promise<* | never>}
 */
export const removeSample = async (projectId, sampleId) =>
  axios
    .delete(`${AJAX_URL}/sample`, {
      data: {
        projectId,
        sampleId,
      },
    })
    .then((response) => response.data);

/**
 * Remove an entire project from the cart.
 * @param {number} id - Identifier for a sample
 * @returns {Promise<{count: any}>}
 */
export const removeProject = async (id) =>
  axios.delete(`${AJAX_URL}/project?id=${id}`).then(({ data }) => data);
