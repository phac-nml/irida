import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import axios from "axios";
import { addKeysToList } from "../../utilities/http-utilities";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`/ajax/metadata/templates`);

export const templateApi = createApi({
  reducerPath: `templateApi`,
  baseQuery: fetchBaseQuery({ baseUrl: BASE_URL }),
  tagTypes: ["MetadataTemplates"],
  endpoints: (build) => ({
    getTemplatesForProject: build.query({
      query: (projectId) => ({
        url: "",
        params: { projectId },
      }),
      providesTags: (result) =>
        result
          ? result.map(({ id }) => ({ type: "MetadataTemplates", id }))
          : ["MetadataTemplates"],
      transformResponse(response) {
        return addKeysToList(response, "template", "identifier");
      },
    }),
    deleteTemplate: build.mutation({
      query: ({ projectId, templateId }) => ({
        url: `/${templateId}`,
        params: { projectId },
        method: `DELETE`,
      }),
      transformResponse(response) {
        return response.message;
      },
      invalidates: ["MetadataTemplates"],
    }),
  }),
});

console.log(templateApi);

export const {
  useGetTemplatesForProjectQuery,
  useDeleteTemplateMutation,
} = templateApi;

/**
 * Create a new metadata template within a project
 * @param {number} projectId - identifier for the project to create the template within.
 * @param {Object} parameters - details about the template (name, desc, and fields)
 * @returns {Promise<any>}
 */
export async function createProjectMetadataTemplate(projectId, parameters) {
  try {
    const { data } = await axios.post(
      `${BASE_URL}?projectId=${projectId}`,
      parameters
    );
    return data;
  } catch (e) {
    return Promise.reject(e.response.data.message);
  }
}

/**
 * Update the details in a metadata template
 * @param {Object} template - the template to update
 * @returns {Promise<*>}
 */
export async function updateMetadataTemplate(template) {
  try {
    const { data } = await axios.put(
      `${BASE_URL}/${template.identifier}`,
      template
    );
    return data.message;
  } catch (e) {
    return Promise.reject(e.response.data.message);
  }
}

/**
 * Remove a metadata template from within a project
 * @param {number} projectId - identifier for a project
 * @param {number} templateId - identifier for a metadata template
 * @returns {Promise<*>}
 */
export async function deleteMetadataTemplate(projectId, templateId) {
  try {
    const { data } = await axios.delete(
      `${BASE_URL}/${templateId}?projectId=${projectId}`
    );
    return data.message;
  } catch (e) {
    return Promise.reject(e.response.data.error);
  }
}

/**
 * Set a default metadata template for a project
 * @param templateIdId Identifier of the metadata template
 * @param projectId Identifier of the project
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function setDefaultMetadataTemplate(projectId, templateId) {
  try {
    const { data } = await axios.post(
      `${BASE_URL}/${templateId}/set-project-default?projectId=${projectId}`
    );
    return data.message;
  } catch (e) {
    return Promise.reject(e.response.data.message);
  }
}
