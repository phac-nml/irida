import { createReducer, createAction } from "@reduxjs/toolkit";

const initialState = {};

/*
Redux action for user account details.
For more information on redux actions see: https://redux-toolkit.js.org/api/createAction
 */
export const setUserDetails = createAction(
  `rootReducers/setUserDetails`,
  (user, admin, locales, allowedRoles, canEditUserInfo, canEditUserStatus, canCreatePasswordReset, mailConfigured) => ({
    payload: { user, admin, locales, allowedRoles, canEditUserInfo, canEditUserStatus, canCreatePasswordReset, mailConfigured },
  })
);

/*
Redux reducer for user account details.
For more information on redux reducers see: https://redux-toolkit.js.org/api/createReducer
 */
export const userReducer = createReducer(initialState, (builder) => {
  builder.addCase(setUserDetails, (state, action) => {
    state.user = action.payload.user;
    state.admin = action.payload.admin;
    state.locales = action.payload.locales;
    state.allowedRoles = action.payload.allowedRoles;
    state.canEditUserInfo = action.payload.canEditUserInfo;
    state.canEditUserStatus = action.payload.canEditUserStatus;
    state.canCreatePasswordReset = action.payload.canCreatePasswordReset;
    state.mailConfigured = action.payload.mailConfigured;
  });
});