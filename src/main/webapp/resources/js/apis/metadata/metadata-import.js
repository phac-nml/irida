import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`/projects`);

export const metadataImportApi = createApi({
  reducerPath: `metadataImportApi`,
  baseQuery: fetchBaseQuery({ baseUrl: BASE_URL }),
  tagTypes: ["MetadataImport"],
  endpoints: (build) => ({
    getMetadataForProject: build.query({
      query: (projectId) => ({
        url: `${projectId}/sample-metadata/upload/getMetadata`,
      }),
    }),
  })
});

export const {
  useGetMetadataForProjectQuery
} = metadataImportApi;
