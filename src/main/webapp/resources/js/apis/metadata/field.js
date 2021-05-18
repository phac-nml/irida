/**
 * Class responsible for ajax call for project sample metadata fields.
 */
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
