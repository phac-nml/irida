import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`ajax/projects/sample-metadata`);

export const metadataImportApi = createApi({
  reducerPath: `metadataImportApi`,
  baseQuery: fetchBaseQuery({ baseUrl: BASE_URL }),
  tagTypes: ["MetadataImport"],
  endpoints: (build) => ({
    getMetadataForProject: build.query({
      query: (projectId) => ({
        url: `/sample-metadata/upload/getMetadata`,
        params: {
          projectId,
        }
      }),
    }),
  })
});

export const {
  useGetMetadataForProjectQuery
} = metadataImportApi;
