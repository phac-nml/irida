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
      provideTags: (result) =>
        result
          ? result.map(({ identifier }) => ({
              type: "MetadataField",
              id: identifier,
            }))
          : ["MetadataField"],
      transformResponse(response) {
        return addKeysToList(response, "field", "id");
      },
    }),
  }),
});

export const { useGetMetadataFieldsForProjectQuery } = fieldsApi;

/**
 * Get a list of a field restrictions
 * @returns {Promise<any>}
 */
export async function getMetadataRestrictions() {
  try {
    const { data } = await axios.get(`${URL}/restrictions`);
    return data;
  } catch (e) {
    return Promise.reject(e.response.data);
  }
}

/**
 * Update a metadata field restriction on a field within a project
 * @param {number} projectId - identifier for a project
 * @param {number} fieldId - identifier for a metadata field
 * @param {string} projectRole - role to update the field to
 * @returns {Promise<any>}
 */
export async function updateProjectMetadataFieldRestriction({
  projectId,
  fieldId,
  projectRole,
}) {
  try {
    const { data } = await axios.patch(
      `${URL}/restrictions?projectId=${projectId}&fieldId=${fieldId}&projectRole=${projectRole}`
    );
    return data;
  } catch (e) {
    return Promise.reject(e.response.data);
  }
}
