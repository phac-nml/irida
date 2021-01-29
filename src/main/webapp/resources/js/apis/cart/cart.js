import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";
import { cartUpdated } from "../../utilities/events-utilities";
import { notification } from "antd";

const AJAX_URL = setBaseUrl(`/ajax/cart`);

const updateCart = (data) => {
  data.notifications.forEach((n) => notification[n.type](n));
  cartUpdated(data.count);
  return data.count;
};

/**
 * Add samples for a project to the cart.
 * @param {number} projectId Identifier for the project the samples are from.
 * @param {array} samples array of sample {ids } to add to cart.
 * @returns {Promise<{count: any}>}
 */
export const putSampleInCart = async (projectId, samples) => {
  const { data } = await axios.post(AJAX_URL, {
    projectId,
    sampleIds: samples.map((s) => s.id),
  });
  return updateCart(data);
};

/**
 * Get the current number of samples in the cart
 * @returns {Promise<{count: any}>}
 */
export const getCartCount = async () => {
  const { data: count } = await axios.get(`${AJAX_URL}/count`);
  cartUpdated(count);
  return count;
};

/**
 * Get the current state of the cart.
 * @returns {Promise<void | never>}
 */
export const getCart = async () =>
  axios.get(`${AJAX_URL}`).then((response) => response.data);

/**
 * Get a list of projects identifiers that are in the cart
 * @returns {Promise<{ids: *}>}
 */
export const getCartIds = async () =>
  axios.get(`${AJAX_URL}/ids`).then((response) => ({ ids: response.data }));

/**
 * Get the samples for projects by their project identifiers.
 * @param ids - List of identifiers for projects in the cart
 * @returns {Promise<AxiosResponse<any>>}
 */
export const getSamplesForProjects = async (ids) =>
  axios
    .get(`${AJAX_URL}/samples?${ids.map((id) => `ids=${id}`).join("&")}`)
    .then(({ data }) =>
      data
        .map((project) => {
          const { samples, ...p } = project;
          return project.samples.map((sample) => ({ ...sample, project: p }));
        })
        .flat()
    );

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
export const removeSample = async (projectId, sampleId) => {
  const { data } = await axios.delete(`${AJAX_URL}/sample/${sampleId}`);
  return updateCart(data);
};

/**
 * Remove an entire project from the cart.
 * @param {number} id - Identifier for a sample
 * @returns {Promise<{count: any}>}
 */
export const removeProject = async (id) => {
  const { data } = await axios.delete(`${AJAX_URL}/project?id=${id}`);
  return updateCart(data);
};
