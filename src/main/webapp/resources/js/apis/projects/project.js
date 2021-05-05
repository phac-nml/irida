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
    updateProjectDetails: build.mutation({
      query: ({ projectId, field, value }) => ({
        url: "/details",
        params: { projectId },
        body: { field, value },
        method: "PUT",
      }),
      invalidatesTags: ["Project"],
    }),
  }),
});

export const {
  useGetProjectDetailsQuery,
  useUpdateProjectDetailsMutation,
} = projectApi;
