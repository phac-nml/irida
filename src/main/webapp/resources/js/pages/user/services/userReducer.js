import { createReducer, createAction } from "@reduxjs/toolkit";

const initialState = {};

/*
Redux action for setting user account details.
For more information on redux actions see: https://redux-toolkit.js.org/api/createAction
 */
export const setUserDetails = createAction(
  `rootReducers/setUserDetails`,
  (user, admin, locales, allowedRoles, canEditUserInfo, canEditUserStatus, canChangePassword, canCreatePasswordReset, mailConfigured) => ({
    payload: { user, admin, locales, allowedRoles, canEditUserInfo, canEditUserStatus, canChangePassword, canCreatePasswordReset, mailConfigured },
  })
);

/*
Redux action for updating user account details.
For more information on redux actions see: https://redux-toolkit.js.org/api/createAction
 */
export const updateUserDetails = createAction(
  `rootReducers/updateUserDetails`,
  (user) => ({
    payload: { user },
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
    state.canChangePassword = action.payload.canChangePassword;
    state.canCreatePasswordReset = action.payload.canCreatePasswordReset;
    state.mailConfigured = action.payload.mailConfigured;
  });

  builder.addCase(updateUserDetails, (state, action) => {
    state.user = action.payload.user;
  });
});