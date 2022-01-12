import { createReducer, createAction } from "@reduxjs/toolkit";

const initialState = {};

/*
Redux action for user account details.
For more information on redux actions see: https://redux-toolkit.js.org/api/createAction
 */
export const setUserDetails = createAction(
  `rootReducers/setUserDetails`,
  (canCreatePasswordReset, mailConfigured) => ({
    payload: { canCreatePasswordReset, mailConfigured },
  })
);

/*
Redux reducer for user account details.
For more information on redux reducers see: https://redux-toolkit.js.org/api/createReducer
 */
export const userReducer = createReducer(initialState, (builder) => {
  builder.addCase(setUserDetails, (state, action) => {
    state.canCreatePasswordReset = action.payload.canCreatePasswordReset;
    state.mailConfigured = action.payload.mailConfigured;
  });
});
