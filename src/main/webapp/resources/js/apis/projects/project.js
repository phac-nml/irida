import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { setBaseUrl } from "../../utilities/url-utilities";

export const projectApi = createApi({
  reducerPath: `projectsApi`,
  baseQuery: fetchBaseQuery({
    baseUrl: setBaseUrl(`ajax/project`),
  }),
  tagTypes: ["Project"],
  endpoints: (build) => ({
    getProjectDetails: build.query({
      query: (projectId) => ({
        url: "/details",
        params: { projectId },
      }),
      providesTags: ["Project"],
    }),
  }),
});

export const { useGetProjectDetailsQuery } = projectApi;
