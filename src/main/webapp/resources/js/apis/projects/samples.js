import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { getProjectIdFromUrl, setBaseUrl } from "../../utilities/url-utilities";

const PROJECT_ID = getProjectIdFromUrl();
const URL = setBaseUrl(`/ajax/projects`);

/**
 * Redux API for handling project samples queries.
 * @type {Api<(args: (string | FetchArgs), api: BaseQueryApi, extraOptions: {}) => MaybePromise<QueryReturnValue<unknown, {status: number, data: unknown} | {status: "FETCH_ERROR", data?: undefined, error: string} | {status: "PARSING_ERROR", originalStatus: number, data: string, error: string} | {status: "CUSTOM_ERROR", data?: unknown, error: string}, FetchBaseQueryMeta>>, {getSampleIdsForProject: *}, string, never, typeof coreModuleName> | Api<(args: (string | FetchArgs), api: BaseQueryApi, extraOptions: {}) => MaybePromise<QueryReturnValue<unknown, {status: number, data: unknown} | {status: "FETCH_ERROR", data?: undefined, error: string} | {status: "PARSING_ERROR", originalStatus: number, data: string, error: string} | {status: "CUSTOM_ERROR", data?: unknown, error: string}, FetchBaseQueryMeta>>, {getSampleIdsForProject: *}, string, never, any>}
 */
export const samplesApi = createApi({
  reducerPath: "samplesApi",
  baseQuery: fetchBaseQuery({
    baseUrl: URL,
  }),
  endpoints: (builder) => ({
    getSampleDropDownForProject: builder.query({
      query: (projectId) => ({ url: `${projectId}/samples/dropdown` }),
    }),
    listSamples: builder.query({
      query: (body) => ({
        url: `/${PROJECT_ID}/samples`,
        method: "POST",
        body,
      }),
    }),
    merge: builder.mutation({
      query: ({ projectId, request }) => ({
        url: `/${PROJECT_ID}/samples/merge`,
        method: "POST",
        body: request,
      }),
    }),
    remove: builder.mutation({
      query: (sampleIds) => ({
        url: `/${PROJECT_ID}/samples/remove`,
        method: "DELETE",
        body: { sampleIds },
      }),
    }),
    shareSamplesWithProject: builder.mutation({
      query: (body) => ({
        url: `/${PROJECT_ID}/samples/share`,
        method: `POST`,
        body,
      }),
    }),
    //TODO: This should not be in the slice but async thunk (update in metadata security)
    getSampleIdsForProject: builder.query({
      query: (projectId) => ({
        url: `/${PROJECT_ID}/samples/identifiers?id=${projectId}`,
      }),
    }),
    getSampleNamesForProject: builder.query({
      query: (projectId) => ({
        url: `/names?id=${projectId}`,
      }),
    }),
  }),
});

export const {
  useGetSampleDropDownForProjectQuery,
  useListSamplesQuery,
  useMergeMutation,
  useRemoveMutation,
  useGetSampleIdsForProjectQuery,
  useGetSampleNamesForProjectQuery,
  useShareSamplesWithProjectMutation,
} = samplesApi;

/**
 * Server side validation of a new sample name.
 * @param {string} name - sample name to validate
 * @param {number} projectId - project identifier
 * @returns {Promise<any>}
 */
export async function validateSampleName(name, projectId = undefined) {
  let url;
  const params = new URLSearchParams();
  params.append("name", name.trim());

  if (projectId === undefined)
    url = `${URL}/${PROJECT_ID}/samples/add-sample/validate?${params}`;
  else url = `${URL}/${projectId}/samples/add-sample/validate?${params}`;

  const response = await fetch(url);
  return response.json();
}

/**
 * Create a new sample within a project
 * @param {string} name - name of the new sample
 * @param {string} organism - name of the organism (optional)
 * @returns {Promise<Response>}
 */
export async function createNewSample({ name, organism }) {
  const response = await fetch(`${URL}/${PROJECT_ID}/samples/add-sample`, {
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

/**
 * Get minimal information for all samples in a project.
 * @param {object} options - current table filters
 * @returns {Promise<*>}
 */
export async function getMinimalSampleDetailsForFilteredProject(options) {
  return await fetch(`${URL}/${PROJECT_ID}/samples/ids`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(options),
  }).then((response) => response.json());
}
