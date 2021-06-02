import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { setBaseUrl } from "../../utilities/url-utilities";

export const createProjectApi = createApi({
  reducerPath: `createProjectApi`,
  baseQuery: fetchBaseQuery({ baseUrl: setBaseUrl(`/ajax/projects/new`) }),
  tagTypes: ["Project"],
  endpoints: (build) => ({}),
});
