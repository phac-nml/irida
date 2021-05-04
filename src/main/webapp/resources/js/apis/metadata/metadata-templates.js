import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
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
        result.map(({ identifier }) => ({
          type: "MetadataTemplate",
          id: identifier,
        })),
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
      invalidatesTags: ["MetadataTemplate"],
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
