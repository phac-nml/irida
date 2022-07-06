/**
 * Class responsible for ajax call for project sample metadata fields.
 */
import axios from "axios";
import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { addKeysToList } from "../../utilities/http-utilities";
import {
  metadata_field_restrictions_route,
  metadata_fields_api_route,
  metadata_fields_in_project_route,
} from "../routes";
import { get } from "../requests";

/**
 * Redux API for metadata fields.
 * @type {Api<(args: (string | FetchArgs), api: BaseQueryApi, extraOptions: {}) => MaybePromise<QueryReturnValue<unknown, FetchBaseQueryError, FetchBaseQueryMeta>>, {getMetadataFieldsForProject: *}, string, string, typeof coreModuleName> | Api<(args: (string | FetchArgs), api: BaseQueryApi, extraOptions: {}) => MaybePromise<QueryReturnValue<unknown, FetchBaseQueryError, FetchBaseQueryMeta>>, {getMetadataFieldsForProject: *}, string, string, typeof coreModuleName | typeof reactHooksModuleName>}
 */
export const fieldsApi = createApi({
  reducerPath: `fieldsApi`,
  baseQuery: fetchBaseQuery({ baseUrl: metadata_fields_api_route() }),
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
  return await get(metadata_field_restrictions_route());
}

/**
 * Get a list of metadata fields for the list of projects
 * @returns {Promise<any>}
 */
export async function getAllMetadataFieldsForProjects({ projectIds }) {
  try {
    const { data } = await axios.get(
      `${metadata_fields_in_project_route()}?projectIds=${projectIds}`
    );
    return addKeysToList(data, "field", "id");
  } catch (e) {
    return Promise.reject(e.response.data.message);
  }
}
