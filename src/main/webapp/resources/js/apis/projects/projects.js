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
  tagTypes: ["Projects"],
  endpoints: (build) => ({
    getProjectsForUser: build.query({
      query: (query) => ({
        url: "/user",
        params: { query },
      }),
      providesTags: ["Projects"],
    }),
  }),
});

export const { useGetProjectsForUserQuery } = projectsApi;

/**
 * Returns the projects on the current page of the projects table.
 * @param {Object} params
 * @returns {Promise<{}|T>}
 */
export async function getPagedProjectsForUser(params) {
  try {
    const { data } = await axios.post(
      `${URL}?admin=${window.location.href.includes("all")}`,
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
