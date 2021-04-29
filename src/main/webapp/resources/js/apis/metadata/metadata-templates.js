import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import axios from "axios";
import { addKeysToList } from "../../utilities/http-utilities";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`/ajax/metadata/templates`);

export const templateApi = createApi({
  reducerPath: `templateApi`,
  baseQuery: fetchBaseQuery({ baseUrl: BASE_URL }),
  tagTypes: ["MetadataTemplate"],
  endpoints: (build) => ({
    getTemplatesForProject: build.query({
      query: (projectId) => ({
        url: "",
        params: { projectId },
      }),
      providesTags: (result) =>
        result
          ? result.map(({ identifier }) => ({
              type: "MetadataTemplate",
              id: identifier,
            }))
          : ["MetadataTemplate"],
      transformResponse(response) {
        return addKeysToList(response, "template", "identifier");
      },
    }),
    createMetadataTemplate: build.mutation({
      query: ({ projectId, template }) => ({
        url: "",
        params: { projectId },
        method: "POST",
        body: template,
      }),
      invalidatesTags: ["MetadataTemplates"],
    }),
    updateMetadataTemplate: build.mutation({
      query: (template) => ({
        url: `/${template.identifier}`,
        method: `PUT`,
        body: template,
      }),
      invalidatesTags: ["MetadataTemplate"],
    }),
    deleteTemplate: build.mutation({
      query: ({ projectId, templateId }) => ({
        url: `/${templateId}`,
        params: { projectId },
        method: `DELETE`,
      }),
      invalidatesTags: ["MetadataTemplate"],
    }),
  }),
});

export const {
  useGetTemplatesForProjectQuery,
  useCreateMetadataTemplateMutation,
  useUpdateMetadataTemplateMutation,
  useDeleteTemplateMutation,
} = templateApi;

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
