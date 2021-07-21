/**
 * @file API the ProjectAjaxController
 */
import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const URL = setBaseUrl(`ajax/projects`);

export const projectsApi = createApi({
  reducerPath: `projectsApi`,
  baseQuery: fetchBaseQuery({ baseUrl: URL }),
  tagTypes: [],
  endpoints: (build) => ({
    getProjectsManagedByUser: build.query({
      query: (currentId) => ({
        url: `/share-samples/projects`,
        params: {
          current: currentId,
        },
      }),
    }),
    getCommonSampleIdentifiers: build.query({
      query: ({ projectId, sampleIds }) => ({
        url: `/share-samples/sampleIds`,
        method: "POST",
        params: {
          projectId,
        },
        body: sampleIds,
      }),
    }),
    shareSamplesToProject: build.mutation({
      query: ({ original, destination, sampleIds, owner, fields }) => ({
        url: `/share-samples/copy`,
        method: "PUT",
        body: { original, destination, sampleIds, owner, fields },
      }),
    }),
  }),
});

export const {
  useGetProjectsManagedByUserQuery,
  useGetCommonSampleIdentifiersQuery,
  useShareSamplesToProjectMutation,
} = projectsApi;

/**
 * Returns the projects on the current page of the projects table.
 * @param {Object} params
 * @returns {Promise<{}|T>}
 */
export async function getPagedProjectsForUser(params) {
  try {
    const { data } = await axios.post(
      `${URL}?admin=${window.location.href.includes("all")}
  `,
      params
    );
    return data;
  } catch (e) {
    return {};
  }
}

/**
 * Get a list of available project roles
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function getProjectRoles() {
  return await axios.get(`${URL}/roles`).then(({ data }) => data);
}

export async function copySamples({
  original,
  destination,
  sampleIds,
  owner,
  fields,
}) {
  return axios.post(`${URL}/share-samples/copy`, {
    original,
    destination,
    sampleIds,
    owner,
    fields,
  });
}
