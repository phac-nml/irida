import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { notification } from "antd";
import axios from "axios";
import { cartUpdated } from "../../utilities/events-utilities";
import { setBaseUrl } from "../../utilities/url-utilities";

const AJAX_URL = setBaseUrl(`/ajax/cart`);

export const cartApi = createApi({
  reducerPath: `cartApi`,
  baseQuery: fetchBaseQuery({ baseUrl: AJAX_URL }),
  tagTypes: ["Samples", "CartCount"],
  endpoints: (build) => ({
    /*
   Get all the samples in the cart.  This is specially formatted for the create new project page
   where samples need to be broken down into locked and unlocked samples.
    */
    getCartSamples: build.query({
      query: () => ({ url: "/all-samples" }),
    }),
    count: build.query({
      query: () => ({ url: "/count" }),
      providesTags: ["CartCount"],
      transformResponse: (response) => {
        cartUpdated(response);
        return response;
      },
    }),
    getCart: build.query({
      query: () => ({
        url: "/samples",
      }),
      providesTags: (result) =>
        result
          ? [
              ...result.map(({ id }) => ({ type: "Samples", id })),
              { type: "Samples", id: "LIST" },
            ]
          : [{ type: "Samples", id: "LIST" }],
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
      invalidatesTags: () => ["CartCount", "Samples"],
      transformResponse: (response) => {
        cartUpdated(0);
        return response;
      },
    }),
    removeProject: build.mutation({
      query: ({ id }) => ({
        url: "/project",
        method: "DELETE",
        params: { id },
      }),
      invalidatesTags: [{ type: "Samples", id: "LIST" }, "CartCount"],
      transformResponse: (response) => {
        cartUpdated(response.count);
        return response.notifications[0];
      },
    }),
    removeSample: build.mutation({
      query: ({ sampleId }) => ({
        url: "/sample",
        method: "DELETE",
        params: { sampleId },
      }),
      transformResponse: (response) => {
        cartUpdated(response.count);
        return response.notifications[0];
      },
      invalidatesTags: (result, error, { sampleId }) => [
        { type: "Samples", id: sampleId },
      ],
    }),
    getCartSampleIds: build.query({
      query: () => ({ url: "/sample-ids" }),
    }),
  }),
});

export const {
  useGetCartSamplesQuery,
  useGetCartSampleIdsQuery,
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
  console.log(projectId);
  console.log(samples);
  const { data } = await axios.post(AJAX_URL, {
    projectId,
    sampleIds: samples.map((s) => s.id || s.identifier),
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
  const { data } = await axios.delete(
    `${AJAX_URL}/sample?sampleId=${sampleId}`
  );
  return updateCart(data);
};
