import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";
import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";

const URL = setBaseUrl(`ajax/samples`);

export const sampleApi = createApi({
  reducerPath: `sampleApi`,
  baseQuery: fetchBaseQuery({
    baseUrl: setBaseUrl(URL),
  }),
  tagTypes: ["SampleDetails"],
  endpoints: (build) => ({
    /*
    Get the default information about a sample
     */
    getSampleDetails: build.query({
      query: (sampleId) => ({
        url: `/${sampleId}/details`,
      }),
      providesTags: ["SampleDetails"],
    }),
    /*
    Update sample details
     */
    updateSampleDetails: build.mutation({
      query: ({ sampleId, field, value }) => ({
        url: `/${sampleId}/details`,
        body: { field, value },
        method: "PUT",
      }),
      invalidatesTags: ["SampleDetails"],
    }),
    getSampleMetadata: build.query({
      query: (sampleId) => ({
        url: `/${sampleId}/metadata`,
        method: `GET`,
        providesTags: ["SampleMetadata"],
      }),
    }),
  }),
});

export const {
  useGetSampleDetailsQuery,
  useUpdateSampleDetailsMutation,
  useGetSampleMetadataQuery,
} = sampleApi;

/**
 * Get file details for a sample
 * @param {number} sampleId - identifier for a sample
 * @param {number} projectId - identifier for a project (if the sample is in the cart), not required.
 * @returns {Promise<any>}
 */
export async function fetchSampleFiles({ sampleId, projectId }) {
  try {
    const response = await axios(
      `${URL}/${sampleId}/files${projectId ? `?projectId=${projectId}` : null}`
    );
    return response.data;
  } catch (e) {
    return Promise.reject(e.response.data);
  }
}
