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
    getMetadataFieldRestriction: build.query({
      query: ({ projectId, metadataFieldId }) => ({
        url: `/metadata-field-restriction?projectId=${projectId}&metadataTemplateFieldId=${metadataFieldId}`,
        method: `GET`,
        providesTags: ["MetadataRestriction"],
      }),
    }),
    addSampleMetadata: build.mutation({
      query: ({
        sampleId,
        projectId,
        metadataField,
        metadataEntry,
        metadataRestriction,
      }) => ({
        url: `/${sampleId}/metadata`,
        body: { projectId, metadataField, metadataEntry, metadataRestriction },
        method: "POST",
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
      query: ({
        sampleId,
        projectId,
        metadataFieldId,
        metadataField,
        metadataEntryId,
        metadataEntry,
        metadataRestriction,
      }) => ({
        url: `/${sampleId}/metadata`,
        body: {
          projectId,
          metadataFieldId,
          metadataField,
          metadataEntryId,
          metadataEntry,
          metadataRestriction,
        },
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
  useGetMetadataFieldRestrictionQuery,
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
