import { setBaseUrl } from "../../utilities/url-utilities";
import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";

import { get, post } from "../requests";

const URL = setBaseUrl(`ajax/samples`);

const SEQUENCE_FILES_AJAX_URL = setBaseUrl("ajax/sequenceFiles");

export type AnalysisState =
  | "NEW"
  | "PREPARING"
  | "PREPARED"
  | "SUBMITTING"
  | "RUNNING"
  | "FINISHED_RUNNING"
  | "COMPLETING"
  | "COMPLETED"
  | "TRANSFERRED"
  | "POST_PROCESSING"
  | "ERROR";

export interface GenomeAssembly {
  createdDate: Date;
  file: string;
  identifier: number;
  label: string;
  links: [];
}

export type ProcessingState =
  | "UNPROCESSED"
  | "QUEUED"
  | "PROCESSING"
  | "FINISHED"
  | "ERROR";

export type ProjectMetadataRole = "LEVEL_1" | "LEVEL_2" | "LEVEL_3" | "LEVEL_4";

export interface SampleAnalyses {
  analysisType: string;
  createdDate: Date;
  id: number;
  name: string;
  state: AnalysisState;
}

export interface SampleGenomeAssembly {
  fileInfo: {
    createdDate: Date;
    file: string;
    fileName: string;
    fileRevisionNumber: number;
    identifier: number;
    label: string;
    links: [];
  };
  fileType: string;
  firstFileSize: string;
}

export interface SampleFiles {
  singles: SampleSequencingObject[];
  paired: SampleSequencingObject[];
  fast5: SampleSequencingObject[];
  assemblies: SampleGenomeAssembly[];
}

export interface SampleFileQCEntry {
  id: number;
  createdDate: Date;
  status: string;
  type: string;
  message: string | null;
}

export interface SampleMetadata {
  metadata: SampleMetadataFieldEntry[];
}

export interface SampleMetadataFieldEntry {
  fieldId: number;
  metadataTemplateField: string;
  metadataEntry: string;
  entryId: number;
  metadataRestriction: ProjectMetadataRole;
}

export interface SampleSequencingObject {
  fileInfo: SequencingObject;
  secondFileSize: string;
  firstFileSize: string;
  processingState: ProcessingState;
  fileType: string;
  qcEntries: SampleFileQCEntry[];
}

export interface SequencingFile {
  createdDate: Date;
  file: string;
  fileName: string;
  identifier: string;
  label: string;
  links: [];
  modifiedDate: Date;
  uploadSha: string;
}

export interface SequencingObject {
  createdDate: Date;
  fileProcessor: string;
  files: SequencingFile[];
  forwardSequenceFile: SequencingFile;
  identifier: number;
  label: string;
  links: [];
  processingState: ProcessingState;
  reverseSequenceFile: SequencingFile;
  sequenceFile: SequencingFile;
  file: SequencingFile;
}

/**
 * Redux API to handle queries based on samples
 */
export const sampleApi = createApi({
  reducerPath: `sampleApi`,
  baseQuery: fetchBaseQuery({
    baseUrl: setBaseUrl(URL),
  }),
  tagTypes: ["SampleDetails", "SampleMetadata"],
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
 * @param {number} sampleId - identifier for a sample
 * @param {number} projectId - identifier for a project
 * @returns {Promise<SampleMetadata>}
 */
export const fetchMetadataForSample = async ({
  sampleId,
  projectId,
}: {
  sampleId: number;
  projectId: number;
}): Promise<SampleMetadata> => {
  return get(setBaseUrl(`${URL}/${sampleId}/metadata?projectId=${projectId}`));
};

/**
 * Get file details for a sample
 * @param {number} sampleId - identifier for a sample
 * @param {number} projectId - identifier for a project (if the sample is in the cart), not required.
 * @returns {Promise<SampleFiles>}
 */
export const fetchSampleFiles = async ({
  sampleId,
  projectId,
}: {
  sampleId: number;
  projectId: number;
}): Promise<SampleFiles> => {
  return get(
    `${URL}/${sampleId}/files${projectId && `?projectId=${projectId}`}`
  );
};

/**
 * Get analyses ran for sample
 * @param {number} sampleId - identifier for a sample
 * @returns {Promise<SampleAnalyses[]>}
 */
export const fetchSampleAnalyses = async ({
  sampleId,
}: {
  sampleId: number;
}): Promise<SampleAnalyses[]> => {
  return get(`${URL}/${sampleId}/analyses`);
};

/**
 * Get updated sequencing objects details for a sample
 * @param {number} sampleId - identifier for a sample
 * @param {number} projectId - identifier for a project
 * @param {array} sequencingObjectIds - identifiers for updated sequencing objects to get.
 * @returns {Promise<SampleFiles>}
 */
export const fetchUpdatedSequencingObjects = ({
  sampleId,
  projectId,
  sequencingObjectIds,
}: {
  sampleId: number;
  projectId: number;
  sequencingObjectIds: number[];
}): Promise<SampleFiles> => {
  return get(
    `${URL}/${sampleId}/updated-sequencing-objects?sequencingObjectIds=${sequencingObjectIds}&projectId=${
      projectId ? projectId : ""
    }`
  );
};

/**
 * Download genome assembly file
 * @param {number} sampleId - identifier for a sample
 * @param {number} genomeAssemblyId - identifier for the genomeassembly to download
 */
export function downloadGenomeAssemblyFile({
  sampleId,
  genomeAssemblyId,
}: {
  sampleId: number;
  genomeAssemblyId: number;
}) {
  window.open(
    `${URL}/${sampleId}/assembly/download?genomeAssemblyId=${genomeAssemblyId}`,
    "_blank"
  );
}

/**
 * Download a sequence file
 * @param {number} sequencingObjectId - identifier for the sequencingobject
 * @param {number} sequenceFileId - identifier for the sequence file to download
 */
export function downloadSequencingObjectFile({
  sequencingObjectId,
  sequenceFileId,
}: {
  sequencingObjectId: number;
  sequenceFileId: number;
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
 * @returns {Promise<SampleSequencingObject[]>}
 */
export const uploadSequenceFiles = async ({
  sampleId,
  formData,
  config,
}: {
  sampleId: number;
  formData: any;
  config: Record<string, unknown>;
}): Promise<SampleSequencingObject[]> => {
  return post(`${URL}/${sampleId}/sequenceFiles/upload`, formData, config);
};

/**
 * Upload assembly files
 * @param sampleId - identifier for the sample
 * @param formData - assembly files to upload
 * @param config - configuration for the upload
 * @returns {Promise<SampleGenomeAssembly[]>}
 */
export const uploadAssemblyFiles = ({
  sampleId,
  formData,
  config,
}: {
  sampleId: number;
  formData: any;
  config: Record<string, unknown>;
}): Promise<SampleGenomeAssembly[]> => {
  return post(`${URL}/${sampleId}/assemblies/upload`, formData, config);
};

/**
 * Upload fast5 files
 * @param sampleId - identifier for the sample
 * @param formData - fast5 files to upload
 * @param config - configuration for the upload
 * @returns {Promise<SampleSequencingObject[]>}
 */
export const uploadFast5Files = ({
  sampleId,
  formData,
  config,
}: {
  sampleId: number;
  formData: any;
  config: Record<string, unknown>;
}): Promise<SampleSequencingObject[]> => {
  return post(`${URL}/${sampleId}/fast5/upload`, formData, config);
};
