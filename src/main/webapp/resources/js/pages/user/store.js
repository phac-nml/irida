import { configureStore } from "@reduxjs/toolkit";
import { userApi } from "../../apis/users/users";
import { settingsApi } from "../../apis/settings/settings";
import { passwordResetApi } from "../../apis/passwordReset";
import {
  projectSubscriptionsApi
} from "../../apis/projects/project-subscriptions";

/*
Redux Store for user details.
For more information on redux stores see: https://redux.js.org/tutorials/fundamentals/part-4-store
 */
export default configureStore({
  reducer: {
    [passwordResetApi.reducerPath]: passwordResetApi.reducer,
    [projectSubscriptionsApi.reducerPath]: projectSubscriptionsApi.reducer,
    [userApi.reducerPath]: userApi.reducer,
    [settingsApi.reducerPath]: settingsApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(
      passwordResetApi.middleware,
      projectSubscriptionsApi.middleware,
      userApi.middleware,
      settingsApi.middleware,
    ),
});
