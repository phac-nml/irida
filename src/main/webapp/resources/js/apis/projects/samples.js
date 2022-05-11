import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { setBaseUrl } from "../../utilities/url-utilities";

const URL = setBaseUrl(`/ajax/projects/${window.project.id}/samples`);

/**
 * Redux API for handling project samples queries.
 * @type {Api<(args: (string | FetchArgs), api: BaseQueryApi, extraOptions: {}) => MaybePromise<QueryReturnValue<unknown, {status: number, data: unknown} | {status: "FETCH_ERROR", data?: undefined, error: string} | {status: "PARSING_ERROR", originalStatus: number, data: string, error: string} | {status: "CUSTOM_ERROR", data?: unknown, error: string}, FetchBaseQueryMeta>>, {getSampleIdsForProject: *}, string, never, typeof coreModuleName> | Api<(args: (string | FetchArgs), api: BaseQueryApi, extraOptions: {}) => MaybePromise<QueryReturnValue<unknown, {status: number, data: unknown} | {status: "FETCH_ERROR", data?: undefined, error: string} | {status: "PARSING_ERROR", originalStatus: number, data: string, error: string} | {status: "CUSTOM_ERROR", data?: unknown, error: string}, FetchBaseQueryMeta>>, {getSampleIdsForProject: *}, string, never, any>}
 */
export const samplesApi = createApi({
  reducerPath: `samplesApi`,
  baseQuery: fetchBaseQuery({
    baseUrl: setBaseUrl(`/ajax/samples`),
  }),
  endpoints: (build) => ({
    getSampleIdsForProject: build.query({
      query: (projectId) => ({
        url: `identifiers?projectId=${projectId}`,
      }),
    }),
    shareSamplesWithProject: build.mutation({
      query: (body) => ({
        url: `share`,
        method: `POST`,
        body,
      }),
    }),
  }),
});

export const {
  useGetSampleIdsForProjectQuery,
  useShareSamplesWithProjectMutation,
} = samplesApi;

/**
 * Server side validation of a new sample name.
 * @param {string} name - sample name to validate
 * @returns {Promise<any>}
 */
export async function validateSampleName(name) {
  const params = new URLSearchParams();
  params.append("name", name.trim());
  const response = await fetch(`${URL}/add-sample/validate?${params}`);
  return response.json();
}

/**
 * Create a new sample within a project
 * @param {string} name - name of the new sample
 * @param {string} organism - name of the organism (optional)
 * @returns {Promise<Response>}
 */
export async function createNewSample({ name, organism }) {
  const response = await fetch(setBaseUrl(`${URL}/add-sample`), {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ name: name.trim(), organism }),
  });

  return response;
}
export async function shareSamplesWithProject({
  currentId,
  sampleIds,
  targetId,
  locked,
  remove,
}) {
  return await fetch(setBaseUrl(`ajax/samples/share`), {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ currentId, sampleIds, targetId, locked, remove }),
  });
}
