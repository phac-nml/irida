/**
 * Class responsible for ajax call for project sample metadata fields.
 */
import axios from "axios";
import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { addKeysToList } from "../../utilities/http-utilities";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`/ajax/metadata/fields`);

/**
 * Redux API for metadata fields.
 * @type {Api<(args: (string | FetchArgs), api: BaseQueryApi, extraOptions: {}) => MaybePromise<QueryReturnValue<unknown, FetchBaseQueryError, FetchBaseQueryMeta>>, {getMetadataFieldsForProject: *}, string, string, typeof coreModuleName> | Api<(args: (string | FetchArgs), api: BaseQueryApi, extraOptions: {}) => MaybePromise<QueryReturnValue<unknown, FetchBaseQueryError, FetchBaseQueryMeta>>, {getMetadataFieldsForProject: *}, string, string, typeof coreModuleName | typeof reactHooksModuleName>}
 */
export const fieldsApi = createApi({
  reducerPath: `fieldsApi`,
  baseQuery: fetchBaseQuery({ baseUrl: BASE_URL }),
  tagTypes: ["MetadataFields"],
  endpoints: (build) => ({
    /*
    Get the metadata fields for a specific project.
     */
    getMetadataFieldsForProject: build.query({
      query: (projectId) => ({
        url: "",
        params: { projectId },
      }),
      provides: (result) => [
        ...result.map(({ id }) => ({ type: "MetadataFields", id })),
        { type: "MetadataFields", id: "LIST" },
      ],
      transformResponse(response) {
        return addKeysToList(response, "field", "id");
      },
    }),
    updateProjectMetadataFieldRestriction: build.mutation({
      query: ({ projectId, fieldId, projectRole }) => ({
        url: `/restrictions`,
        method: `PATCH`,
        params: { projectId, fieldId, projectRole },
      }),
      invalidatesTags: [{ type: "MetadataFields", id: "LIST" }],
    }),
  }),
});

export const {
  useGetMetadataFieldsForProjectQuery,
  useUpdateProjectMetadataFieldRestrictionMutation,
} = fieldsApi;

/**
 * Get a list of field restrictions
 * @returns {Promise<any>}
 */
export async function getMetadataRestrictions() {
  try {
    const { data } = await axios.get(`${BASE_URL}/restrictions`);
    return data;
  } catch (e) {
    return Promise.reject(e.response.data.message);
  }
}

/**
 * Get a list of metadata fields for the list of projects
 * @returns {Promise<any>}
 */
export async function getAllMetadataFieldsForProjects({ projectIds }) {
  try {
    const { data } = await axios.get(
      `${BASE_URL}/projects?projectIds=${projectIds}`
    );
    return addKeysToList(data, "field", "id");
  } catch (e) {
    return Promise.reject(e.response.data.message);
  }
}
