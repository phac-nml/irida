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
      /**
      Transforming the response to include a unique row key for rendering the ant design table.
      */
      transformResponse(response) {
        const transformed = {
          ...response,
          projects: response.projects.map((project, index) => ({
            ...project,
            rowKey: `user-projects-row-${index}`,
          })),
        };
        return transformed;
      },
      providesTags: ["Projects"],
    }),
    /*
    Edit user details.
    */
    editUserDetails: build.mutation({
      query: ({
        userId,
        firstName,
        lastName,
        email,
        phoneNumber,
        role,
        locale,
        enabled,
      }) => ({
        url: `/${userId}/edit`,
        body: {
          firstName,
          lastName,
          email,
          phoneNumber,
          systemRole: role,
          locale,
          enabled,
        },
        method: "POST",
      }),
      invalidatesTags: ["Users"],
    }),
    /*
    Change user password.
    */
    changeUserPassword: build.mutation({
      query: ({ userId, oldPassword, newPassword }) => ({
        url: `/${userId}/changePassword`,
        params: { oldPassword, newPassword },
        method: "POST",
      }),
      invalidatesTags: ["Users"],
    }),
    /*
    Update the disabled status of a user by user id.
    */
    setUsersDisabledStatus: build.mutation({
      query: ({ isEnabled, id }) => ({
        url: `/edit`,
        params: { isEnabled, id },
        method: "PUT",
      }),
    }),
  }),
});

export const {
  useGetUserDetailsQuery,
  useGetUserProjectDetailsQuery,
  useEditUserDetailsMutation,
  useChangeUserPasswordMutation,
  useSetUsersDisabledStatusMutation
} = usersApi;
