import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";
import moment from 'moment';

const BASE_URL = setBaseUrl(`ajax/users`);

/**
 * Redux API for users.
 */
export const usersApi = createApi({
  reducerPath: `usersApi`,
  baseQuery: fetchBaseQuery({
    baseUrl: setBaseUrl(BASE_URL),
  }),
  tagTypes: ["Users", "Projects"],
  endpoints: (build) => ({
    /*
    Get user details.
     */
    getUserDetails: build.query({
      query: (userId) => ({
        url: `/${userId}`,
      }),
      providesTags: ["Users"],
    }),
    /*
    Get user projects.
    */
    getUserProjectDetails: build.query({
      query: (userId) => ({
        url: `/${userId}/projects/list`,
      }),
        providesTags: ["Projects"],
    }),
    /*
    Edit user details.
    */
    editUserDetails: build.mutation({
      query: ({ userId, firstName, lastName, email, phoneNumber, role, locale, enabled }) => ({
        url: `/${userId}/edit`,
        params: { firstName, lastName, email, phoneNumber, systemRole: role, locale, enabled },
        method: "POST",
      }),
      invalidatesTags: ["Users"],
    }),
  }),
});

export const {
  useGetUserDetailsQuery,
  useGetUserProjectDetailsQuery,
  useEditUserDetailsMutation
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
