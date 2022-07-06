import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { validateSampleName } from "./sample-utils";
import { metadata_upload_api } from "../routes";

/**
 * Redux API for Sample Metadata
 */
export const metadataImportApi = createApi({
  reducerPath: `metadataImportApi`,
  baseQuery: fetchBaseQuery({ baseUrl: metadata_upload_api() }),
  tagTypes: ["MetadataImport"],
  endpoints: (build) => ({
    getProjectSampleMetadata: build.query({
      query: (projectId) => ({
        url: `/getMetadata`,
        params: {
          projectId,
        },
      }),
      /**
      Transforming the response to include if the row has a valid sample name and a unique row key for rendering the ant design table.
      */
      transformResponse(response) {
        const transformed = {
          ...response,
          rows: response.rows.map((row, index) => ({
            ...row,
            rowKey: `row-${index}`,
            isSampleNameValid: validateSampleName(
              row.entry[response.sampleNameColumn]
            ),
          })),
        };
        return transformed;
      },
      providesTags: ["MetadataImport"],
    }),
    clearProjectSampleMetadata: build.mutation({
      query: (projectId) => ({
        url: `/clear`,
        method: "DELETE",
        params: {
          projectId,
        },
      }),
      invalidatesTags: ["MetadataImport"],
    }),
    setColumnProjectSampleMetadata: build.mutation({
      query: ({ projectId, sampleNameColumn }) => ({
        url: `/setSampleColumn`,
        method: "PUT",
        params: {
          projectId,
          sampleNameColumn,
        },
      }),
      invalidatesTags: ["MetadataImport"],
    }),
    saveProjectSampleMetadata: build.mutation({
      query: ({ projectId, sampleNames }) => ({
        url: `/save`,
        method: "POST",
        params: {
          projectId,
          sampleNames,
        },
      }),
      invalidatesTags: ["MetadataImport"],
    }),
  }),
});

export const {
  useGetProjectSampleMetadataQuery,
  useClearProjectSampleMetadataMutation,
  useSetColumnProjectSampleMetadataMutation,
  useSaveProjectSampleMetadataMutation,
} = metadataImportApi;
