import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`ajax/projects/sample-metadata/upload`);

/**
 * Redux API for Sample Metadata
 */
export const metadataImportApi = createApi({
  reducerPath: `metadataImportApi`,
  baseQuery: fetchBaseQuery({ baseUrl: BASE_URL }),
  tagTypes: ["MetadataImport"],
  endpoints: (build) => ({
    getProjectSampleMetadata: build.query({
      query: (projectId) => ({
        url: `/getMetadata`,
        params: {
          projectId,
        }
      }),
      transformResponse(response) {
        const transformed = {...response, rows: response.rows.map((row, index) => ({...row, rowKey: `row-${index}` }))}
        return transformed;
      },
      providesTags: ["MetadataImport"],
    }),
    clearProjectSampleMetadata: build.mutation({
      query: (projectId) => ({
        url: `/clear`,
        method: 'DELETE',
        params: {
          projectId,
        }
      }),
      invalidatesTags: ["MetadataImport"],
    }),
    setColumnProjectSampleMetadata: build.mutation({
      query: ({ projectId, sampleNameColumn }) => ({
        url: `/setSampleColumn`,
        method: 'PUT',
        params: {
          projectId,
          sampleNameColumn,
        }
      }),
      invalidatesTags: ["MetadataImport"],
    }),
    saveProjectSampleMetadata: build.mutation({
      query: ({ projectId, sampleNames }) => ({
        url: `/save`,
        method: 'POST',
        params: {
          projectId,
          sampleNames
        }
      }),
      invalidatesTags: ["MetadataImport"],
    }),
  })
});

export const {
  useGetProjectSampleMetadataQuery,
  useClearProjectSampleMetadataMutation,
  useSetColumnProjectSampleMetadataMutation,
  useSaveProjectSampleMetadataMutation,
} = metadataImportApi;
