/**
 * Class responsible for ajax call for project sample metadata fields.
 */
import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import axios from "axios";
import { addKeysToList } from "../../utilities/http-utilities";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`/ajax/metadata/fields`);

/**
 * Get all metadata fields associated with samples in a given project.
 * @param {number} projectId - identifier for a project
 * @returns {Promise<any>}
 */
export async function getMetadataFieldsForProject(projectId) {
  try {
    const { data } = await axios.get(`${BASE_URL}?projectId=${projectId}`);
    return data;
  } catch (e) {
    return e.response.data.message;
  }
}

export const fieldsApi = createApi({
  reducerPath: `fieldsApi`,
  baseQuery: fetchBaseQuery({ baseUrl: BASE_URL }),
  tagTypes: ["MetadataFields"],
  endpoints: (build) => ({
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
