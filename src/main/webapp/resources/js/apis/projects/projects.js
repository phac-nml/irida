/**
 * @file API the ProjectAjaxController
 */
import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const URL = setBaseUrl(`ajax/projects`);

/**
 * Redux API to handle queries based on projects
 * @type {Api<(args: (string | FetchArgs), api: BaseQueryApi, extraOptions: {}) => MaybePromise<QueryReturnValue<unknown, {status: number, data: unknown} | {status: "FETCH_ERROR", data?: undefined, error: string} | {status: "PARSING_ERROR", originalStatus: number, data: string, error: string} | {status: "CUSTOM_ERROR", data?: unknown, error: string}, FetchBaseQueryMeta>>, {getPotentialProjectsToShareTo: *}, string, string, typeof coreModuleName> | Api<(args: (string | FetchArgs), api: BaseQueryApi, extraOptions: {}) => MaybePromise<QueryReturnValue<unknown, {status: number, data: unknown} | {status: "FETCH_ERROR", data?: undefined, error: string} | {status: "PARSING_ERROR", originalStatus: number, data: string, error: string} | {status: "CUSTOM_ERROR", data?: unknown, error: string}, FetchBaseQueryMeta>>, {getPotentialProjectsToShareTo: *}, string, string, any>}
 */
export const projectsApi = createApi({
  reducerPath: `projectsApi`,
  baseQuery: fetchBaseQuery({ baseUrl: URL }),
  tagTypes: ["Projects"],
  endpoints: (build) => ({
    getPotentialProjectsToShareTo: build.query({
      query: (currentId) => ({
        url: `/samples-share/projects?currentId=${currentId}`,
      }),
    }),
    getProjectNamesForUser: build.query({
      query: () => ({
        url: `/names`,
      }),
    }),
  }),
});

export const {
  useGetPotentialProjectsToShareToQuery,
  useGetProjectNamesForUserQuery,
} = projectsApi;

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
