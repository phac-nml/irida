import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`ajax/project/details`);

export const projectApi = createApi({
  reducerPath: `projectsApi`,
  baseQuery: fetchBaseQuery({
    baseUrl: BASE_URL,
  }),
  tagTypes: ["Project", "MetadataTemplate"],
  endpoints: (build) => ({
    getProjectDetails: build.query({
      query: (projectId) => ({
        url: "",
        params: { projectId },
      }),
      providesTags: ["Project"],
    }),
    updateProjectDetails: build.mutation({
      query: ({ projectId, field, value }) => ({
        url: "",
        params: { projectId },
        body: { field, value },
        method: "PUT",
      }),
      invalidatesTags: ["Project"],
    }),
    getProjectCoverage: build.query({
      query: (projectId) => ({
        url: "/coverage",
        params: { projectId },
      }),
    }),
    updateProjectCoverage: build.mutation({
      query: ({ projectId, coverage }) => ({
        url: "/coverage",
        params: { projectId },
        body: { coverage },
      }),
    }),
    updateProjectPriority: build.mutation({
      query: ({ projectId, priority }) => ({
        url: "/priority",
        method: "PUT",
        params: {
          projectId,
          priority,
        },
      }),
      invalidatesTags: ["Project"],
    }),
    updateDefaultMetadataTemplate: build.mutation({
      query: ({ projectId, templateId }) => ({
        url: "/set-project-default",
        method: "POST",
        params: {
          projectId,
          templateId,
        },
      }),
      invalidatesTags: ["Project"],
    }),
  }),
});

export const {
  useGetProjectDetailsQuery,
  useUpdateProjectDetailsMutation,
  useGetProjectCoverageQuery,
  useUpdateProjectCoverageMutation,
  useUpdateProjectPriorityMutation,
  useUpdateDefaultMetadataTemplateMutation,
} = projectApi;

export async function deleteProject(projectId) {
  try {
    await fetch(`${BASE_URL}/delete?projectId=${projectId}`, {
      method: "POST",
      redirect: "follow",
    });
    window.location.href = setBaseUrl("/projects");
  } catch (e) {
    return Promise.reject(e.response.data.error);
  }
}
