/**
 * Class responsible for ajax call for project sample metadata fields.
 */
import axios from "axios";
import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { addKeysToList } from "../../utilities/http-utilities";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`/ajax/metadata/fields`);

export interface MetadataField {
  id?: number;
  fieldKey?: string;
  label: string;
  type?: string;
  restriction: string;
}

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
      providesTags: (result) => [
        ...result.map(({ id }: { id: number }) => ({
          type: "MetadataFields",
          id,
        })),
        { type: "MetadataFields", id: "LIST" },
      ],
      transformResponse(response: MetadataField[]) {
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
 * Get a list of fields for the project
 * @returns {Promise<any>}
 */
export async function getMetadataFieldsForProject(projectId: string) {
  try {
    const { data } = await axios.get(`${BASE_URL}?projectId=${projectId}`);
    return data;
  } catch (error) {
    if (axios.isAxiosError(error)) {
      if (error.response) {
        return Promise.reject(error.response.data.message);
      } else {
        return Promise.reject(error.message);
      }
    } else {
      return Promise.reject("An unexpected error occurred");
    }
  }
}

/**
 * Get a list of field restrictions
 * @returns {Promise<any>}
 */
export async function getMetadataRestrictions() {
  try {
    const { data } = await axios.get(`${BASE_URL}/restrictions`);
    return data;
  } catch (error) {
    if (axios.isAxiosError(error)) {
      if (error.response) {
        return Promise.reject(error.response.data.message);
      } else {
        return Promise.reject(error.message);
      }
    } else {
      return Promise.reject("An unexpected error occurred");
    }
  }
}

/**
 * Get a list of metadata fields for the list of projects
 * @returns {Promise<any>}
 */
export async function getAllMetadataFieldsForProjects(
  projectIds: string[]
): Promise<MetadataField[]> {
  try {
    const { data } = await axios.get(
      `${BASE_URL}/projects?projectIds=${projectIds}`
    );
    return addKeysToList(data, "field", "id");
  } catch (error) {
    if (axios.isAxiosError(error)) {
      if (error.response) {
        return Promise.reject(error.response.data.message);
      } else {
        return Promise.reject(error.message);
      }
    } else {
      return Promise.reject("An unexpected error occurred");
    }
  }
}

/*
 * Create metadata fields for a specific project
 * @returns {Promise<any>}
 */
export async function createMetadataFieldsForProject({
  projectId,
  body,
}: {
  projectId: string;
  body: MetadataField[];
}) {
  return await axios.post(`${BASE_URL}?projectId=${projectId}`, body);
}
