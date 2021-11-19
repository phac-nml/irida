import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`ajax/users`);

/**
 * Redux API for users.
 */
export const usersApi = createApi({
  reducerPath: `usersApi`,
  baseQuery: fetchBaseQuery({
    baseUrl: setBaseUrl(BASE_URL),
  }),
  tagTypes: ["Users"],
  endpoints: (build) => ({
    /*
    Get details for a user.
     */
    getUserDetails: build.query({
      query: (userId) => ({
        url: `/${userId}`,
      }),
      providesTags: ["Users"],
      transformResponse: (response) => console.log(response)
    }),
    /*
    Get details for the current user.
    */
    getCurrentUserDetails: build.query({
      query: () => ({
        url: "/current/here",
      }),
      providesTags: ["Users"],
      transformResponse: (response) => console.log(response)
    }),
  }),
});

export const {
  useGetUserDetailsQuery,
  useGetCurrentUserDetailsQuery,
} = usersApi;

/**
 * Update the disabled status of a user by user id
 * @param {boolean} isEnabled - the new state of the user
 * @param {number} id - identifier for the user
 * @returns {Promise<AxiosResponse<T>>}
 */
export async function setUsersDisabledStatus({ isEnabled, id }) {
  try {
    return await axios.put(`${BASE_URL}/edit?isEnabled=${isEnabled}&id=${id}`);
  } catch (e) {
    console.log(e);
  }
}
