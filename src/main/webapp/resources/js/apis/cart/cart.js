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

/**
 * Get the current number of samples in the cart
 * @returns {Promise<void>}
 */
export const getCartCount = async () => {
  return axios.get(`${url}/count`).then(response => ({ count: response.data }));
};

/**
 * Get the current state of the cart.
 * @returns {Promise<void | never>}
 */
export const getCart = async () =>
  axios.get(`${url}`).then(response => response.data);

export const getCartIds = async () =>
  axios.get(`${url}/ids`).then(response => ({ ids: response.data }));

export const getSamplesForProjects = async ids =>
  axios
    .get(`${url}?${ids.map(id => `projectId=${id}`).join("&")}`)
    .then(({ data }) => data);
// {
//
//   const samples = [];
//   ids.forEach(id =>
//     axios.get(`${url}?projectIds=${id}`).then(({ data }) => samples.concat(data))
//   );
//   return samples;
// };

/**
 * Remove all samples from the cart
 */
export const emptyCart = async () => axios.delete(`${url}`);

/**
 * Remove an individual sample from the cart.
 * @param {number} projectId - Identifier for a project
 * @param {number} sampleId - Identifier for a sample
 * @returns {Promise<* | never>}
 */
export const removeSample = async (projectId, sampleId) =>
  axios
    .delete(`${url}/sample`, {
      data: {
        projectId,
        sampleId
      }
    })
    .then(response => response.data);

/**
 * Remove an entire project from the cart.
 * @param {number} id - Identifier for a sample
 * @returns {Promise<AxiosPromise>}
 */
export const removeProject = async id =>
  axios.delete(`${url}/project?id=${id}`).then(response => response.data);
