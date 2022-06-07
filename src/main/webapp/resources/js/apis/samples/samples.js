
import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";
import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";

const URL = setBaseUrl(`ajax/samples`);

const SEQUENCE_FILES_AJAX_URL = setBaseUrl("ajax/sequenceFiles");

/**
 * Redux API to handle queries based on samples
 */
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
      query: ({ sampleId, projectId }) => ({
        url: `/${sampleId}/details?projectId=${projectId}`,
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
      query: ({ field, entryId, projectId }) => ({
        url: `/metadata?projectId=${projectId}&metadataField=${field}&metadataEntryId=${entryId}`,
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
    removeSampleFiles: build.mutation({
      query: ({ sampleId, fileObjectId, type }) => ({
        url: `/${sampleId}/files?fileObjectId=${fileObjectId}&fileType=${type}`,
        method: "DELETE",
      }),
    }),
    concatenateSequencingObjects: build.mutation({
      query: ({
        sampleId,
        sequencingObjectIds,
        newFileName,
        removeOriginals,
      }) => ({
        url: `/${sampleId}/files/concatenate?sequencingObjectIds=${sequencingObjectIds}&newFileName=${newFileName}&removeOriginals=${removeOriginals}`,
        method: "POST",
      }),
    }),
    updateDefaultSampleSequencingObject: build.mutation({
      query: ({ sampleId, sequencingObjectId }) => ({
        url: `/${sampleId}/default-sequencing-object?sequencingObjectId=${sequencingObjectId}`,
        method: "PUT",
      }),
      invalidatesTags: ["SampleDetails"],
    }),
    updateDefaultSampleGenomeAssembly: build.mutation({
      query: ({ sampleId, genomeAssemblyId }) => ({
        url: `/${sampleId}/default-genome-assembly?genomeAssemblyId=${genomeAssemblyId}`,
        method: "PUT",
      }),
      invalidatesTags: ["SampleDetails"],
    }),
  }),
});

export const {
  useGetSampleDetailsQuery,
  useUpdateSampleDetailsMutation,
  useAddSampleMetadataMutation,
  useRemoveSampleMetadataMutation,
  useUpdateSampleMetadataMutation,
  useRemoveSampleFilesMutation,
  useConcatenateSequencingObjectsMutation,
  useUpdateDefaultSampleGenomeAssemblyMutation,
  useUpdateDefaultSampleSequencingObjectMutation,
} = sampleApi;

/**
 * Gets the sample metadata.
 * @param {Object} params
 * @returns {Promise<{}|T>}
 */
export const fetchMetadataForSample = async ({ sampleId, projectId }) => {
  try {
    const { data } = await axios.get(
      setBaseUrl(`${URL}/${sampleId}/metadata?projectId=${projectId}`)
    );
    return data;
  } catch (e) {
    return Promise.reject();
  }
};

/**
 * Get file details for a sample
 * @param {number} sampleId - identifier for a sample
 * @param {number} projectId - identifier for a project (if the sample is in the cart), not required.
 * @returns {Promise<any>}
 */
export async function fetchSampleFiles({ sampleId, projectId }) {
  try {
    const response = await axios.get(
      `${URL}/${sampleId}/files${projectId && `?projectId=${projectId}`}`
    );
    return response.data;
  } catch (e) {
    return Promise.reject(e.response.data);
  }
}



/**
 * Get analyses ran for sample
 * @param {number} sampleId - identifier for a sample
 * @returns {Promise<any>}
 */
export async function fetchSampleAnalyses({ sampleId }) {
  try {
    const response = await axios.get(`${URL}/${sampleId}/analyses`);
    return response.data;
  } catch (e) {
    return Promise.reject(e.response.data);
  }
}

/**
 * Get updated sequencing objects details for a sample
 * @param {number} sampleId - identifier for a sample
 * @param {number} projectId - identifier for a project (if the sample is in the cart), not required.
 * @param {array} sequencingObjectIds - identifiers for updated sequencing objects to get.
 * @returns {Promise<any>}
 */
export async function fetchUpdatedSequencingObjects({
  sampleId,
  projectId,
  sequencingObjectIds,
}) {
  try {
    const response = await axios.get(
      `${URL}/${sampleId}/updated-sequencing-objects?sequencingObjectIds=${sequencingObjectIds}&projectId=${
        projectId ? projectId : ""
      }`
    );
    return response.data;
  } catch (e) {
    return Promise.reject(e.response.data);
  }
}

/**
 * Download genome assembly file
 * @param {number} sampleId - identifier for a sample
 * @param {number} genomeAssemblyId - identifier for the genomeassembly to download
 * @returns {Promise<any>}
 */
export function downloadGenomeAssemblyFile({ sampleId, genomeAssemblyId }) {
  window.open(
    `${URL}/${sampleId}/assembly/download?genomeAssemblyId=${genomeAssemblyId}`,
    "_blank"
  );
}

/**
 * Download a sequence file
 * @param {number} sequencingObjectId - identifier for the sequencingobject
 * @param {number} sequenceFileId - identifier for the sequence file to download
 * @returns {Promise<any>}
 */
export function downloadSequencingObjectFile({
  sequencingObjectId,
  sequenceFileId,
}) {
  window.open(
    `${SEQUENCE_FILES_AJAX_URL}/download?sequencingObjectId=${sequencingObjectId}&sequenceFileId=${sequenceFileId}`,
    "_blank"
  );
}

/**
 * Upload sequence files
 * @param sampleId - identifier for the sample
 * @param formData - sequence files to upload
 * @param config - configuration for the upload
 * @returns {Promise<any>}
 */
export async function uploadSequenceFiles({ sampleId, formData, config }) {
  try {
    const response = await axios.post(
      `${URL}/${sampleId}/sequenceFiles/upload`,
      formData,
      config
    );
    return response.data;
  } catch (e) {
    return Promise.reject(e.response.data);
  }
}

/**
 * Upload assembly files
 * @param sampleId - identifier for the sample
 * @param formData - assembly files to upload
 * @param config - configuration for the upload
 * @returns {Promise<any>}
 */
export async function uploadAssemblyFiles({ sampleId, formData, config }) {
  try {
    const response = await axios.post(
      `${URL}/${sampleId}/assemblies/upload`,
      formData,
      config
    );
    return response.data;
  } catch (e) {
    return Promise.reject(e.response.data);
  }
}

/**
 * Upload fast5 files
 * @param sampleId - identifier for the sample
 * @param formData - fast5 files to upload
 * @param config - configuration for the upload
 * @returns {Promise<any>}
 */
export async function uploadFast5Files({ sampleId, formData, config }) {
  try {
    const response = await axios.post(
      `${URL}/${sampleId}/fast5/upload`,
      formData,
      config
    );
    return response.data;
  } catch (e) {
    return Promise.reject(e.response.data);
  }
}

