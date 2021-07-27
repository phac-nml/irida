import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { notification } from "antd";
import axios from "axios";
import { cartUpdated } from "../../utilities/events-utilities";
import { setBaseUrl } from "../../utilities/url-utilities";

const AJAX_URL = setBaseUrl(`/ajax/cart`);

export const cartApi = createApi({
  reducerPath: `cartApi`,
  baseQuery: fetchBaseQuery({ baseUrl: AJAX_URL }),
  tagTypes: ["Sample", "cart", "CartCount"],
  endpoints: (build) => ({
    count: build.query({
      query: () => ({ url: "/count" }),
      providesTags: (result) => [{ type: "CartCount", id: "LIST" }],
      transformResponse: (response) => {
        console.log(response);
        cartUpdated(response);
        return response;
      },
    }),
    getCart: build.query({
      query: () => ({
        url: "/samples",
        providesTags: (result) =>
          result
            ? [
                ...result.map(({ id }) => ({ type: "Sample", id })),
                { type: "Sample", id: "LIST" },
              ]
            : [{ type: "Sample", id: "LIST" }],
      }),
      transformResponse(response, meta) {
        return response
          .map((project) => {
            const { samples, ...p } = project;
            return project.samples.map((sample) => ({ ...sample, project: p }));
          })
          .flat();
      },
    }),
    empty: build.mutation({
      query: () => ({ url: "", method: "DELETE" }),
      invalidatesTags: () => ["CartCount", { type: "CartCount", id: "LIST" }],
    }),
    removeProject: build.mutation({
      query: ({ id }) => ({
        url: "/project",
        params: { id },
      }),
      invalidatesTags: [{ type: "Sample", id: "LIST" }, "CartCount"],
    }),
    removeSample: build.mutation({
      query: ({ sampleId }) => ({
        url: "/samples",
        method: "DELETE",
        params: { sampleId },
      }),
      invalidatesTags: (result, error, id) => [{ type: "Sample", id }],
    }),
  }),
});

export const {
  useGetCartQuery,
  useCountQuery,
  useEmptyMutation,
  useRemoveProjectMutation,
  useRemoveSampleMutation,
} = cartApi;

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
