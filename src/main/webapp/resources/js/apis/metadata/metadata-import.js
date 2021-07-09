import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`ajax/projects/sample-metadata`);

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
