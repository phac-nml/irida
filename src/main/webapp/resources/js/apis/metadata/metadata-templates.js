import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { addKeysToList } from "../../utilities/http-utilities";
import { metadata_templates_api_route } from "../routes";

/**
 * API to get information about metadata templates
 * @type {Api<(args: (string | FetchArgs), api: BaseQueryApi, extraOptions: {}) => MaybePromise<QueryReturnValue<unknown, FetchBaseQueryError, FetchBaseQueryMeta>>, {getTemplatesForProject: *, updateMetadataTemplate: *, deleteTemplate: *, createMetadataTemplate: *}, string, string, typeof coreModuleName> | Api<(args: (string | FetchArgs), api: BaseQueryApi, extraOptions: {}) => MaybePromise<QueryReturnValue<unknown, FetchBaseQueryError, FetchBaseQueryMeta>>, {getTemplatesForProject: *, updateMetadataTemplate: *, deleteTemplate: *, createMetadataTemplate: *}, string, string, typeof coreModuleName | typeof reactHooksModuleName>}
 */
export const templateApi = createApi({
  reducerPath: `templateApi`,
  baseQuery: fetchBaseQuery({ baseUrl: metadata_templates_api_route() }),
  tagTypes: ["MetadataTemplate"],
  endpoints: (build) => ({
    /*
    Get all metadata templates for a project
     */
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
        return addKeysToList(
          response.sort((a, b) => b.createdDate - a.createdDate),
          "template",
          "identifier"
        );
      },
    }),
    /*
    Create a new metadata template
     */
    createMetadataTemplate: build.mutation({
      query: ({ projectId, template }) => ({
        url: "",
        params: { projectId },
        method: "POST",
        body: template,
      }),
      invalidatesTags: ["MetadataTemplate"],
    }),
    /*
    Update an existing metadata template
     */
    updateMetadataTemplate: build.mutation({
      query: (template) => ({
        url: `/${template.identifier}`,
        method: `PUT`,
        body: template,
      }),
      invalidatesTags: ["MetadataTemplate"],
    }),
    /*
    Delete a metadata template
     */
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
