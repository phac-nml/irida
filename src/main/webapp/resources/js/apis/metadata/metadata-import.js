import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`ajax/projects/sample-metadata`);

/**
 * Redux API for Sample Metadata
 * @type {Api<(args: (string | FetchArgs), api: BaseQueryApi, extraOptions: {}) => MaybePromise<QueryReturnValue<unknown, FetchBaseQueryError, FetchBaseQueryMeta>>, {createProjectSampleMetadata: *, clearProjectSampleMetadata: *}, string, string, typeof coreModuleName> | Api<(args: (string | FetchArgs), api: BaseQueryApi, extraOptions: {}) => MaybePromise<QueryReturnValue<unknown, FetchBaseQueryError, FetchBaseQueryMeta>>, {createProjectSampleMetadata: *, clearProjectSampleMetadata: *}, string, string, typeof coreModuleName | typeof reactHooksModuleName>}
 */
export const metadataImportApi = createApi({
  reducerPath: `metadataImportApi`,
  baseQuery: fetchBaseQuery({ baseUrl: BASE_URL }),
  tagTypes: ["MetadataImport"],
  endpoints: (build) => ({
    getProjectSampleMetadata: build.query({
      query: (projectId) => ({
        url: `/upload/getMetadata`,
        params: {
          projectId,
        }
      }),
      providesTags: ["MetadataImport"],
    }),
    clearProjectSampleMetadata: build.mutation({
      query: (projectId) => ({
      url: `/upload/clear`,
        params: {
          projectId,
        }
      }),
      invalidatesTags: ["MetadataImport"],
    }),
    setColumnProjectSampleMetadata: build.mutation({
      query: ({projectId, sampleNameColumn}) => ({
        url: `/upload/setSampleColumn`,
        method: 'POST',
        params: {
          projectId,
          sampleNameColumn,
        }
      }),
      invalidatesTags: ["MetadataImport"],
    }),
    saveProjectSampleMetadata: build.mutation({
      query: ({projectId, sampleNameColumn}) => ({
        url: `/upload/save`,
        method: 'POST',
        params: {
          projectId,
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
