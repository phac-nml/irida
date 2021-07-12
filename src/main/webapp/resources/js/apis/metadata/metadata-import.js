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
    createProjectSampleMetadata: build.query({
      query: (projectId) => ({
        url: `/upload/getMetadata`,
        params: {
          projectId,
        }
      }),
    }),
    clearProjectSampleMetadata: build.query({
      query: (projectId) => ({
        url: `/upload/clear`,
        params: {
          projectId,
        }
      }),
    }),
  })
});

export const {
  useCreateProjectSampleMetadataQuery,
  useClearProjectSampleMetadataQuery,
} = metadataImportApi;
