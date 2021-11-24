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
    addSampleMetadata: build.mutation({
      query: ({ sampleId, metadataField, metadataEntry }) => ({
        url: `/${sampleId}/metadata?metadataField=${metadataField}&metadataEntry=${metadataEntry}`,
        method: "PUT",
      }),
      invalidatesTags: ["SampleMetadata"],
    }),
    removeSampleMetadata: build.mutation({
      query: ({ field, entryId }) => ({
        url: `/metadata?metadataField=${field}&metadataEntryId=${entryId}`,
        method: "DELETE",
      }),
      invalidatesTags: ["SampleMetadata"],
    }),
    updateSampleMetadata: build.mutation({
      query: ({ sampleId, metadataField, metadataEntry }) => ({
        url: `/${sampleId}/metadata/update?metadataField=${metadataField}&metadataEntry=${metadataEntry}`,
        method: "PUT",
      }),
      invalidatesTags: ["SampleMetadata"],
    }),
  }),
});

export const {
  useGetSampleDetailsQuery,
  useUpdateSampleDetailsMutation,
  useGetSampleMetadataQuery,
  useAddSampleMetadataMutation,
  useRemoveSampleMetadataMutation,
  useUpdateSampleMetadataMutation,
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
      `${URL}/${sampleId}/files${projectId && `?projectId=${projectId}`}`
    );
    return response.data;
  } catch (e) {
    return Promise.reject(e.response.data);
  }
}
