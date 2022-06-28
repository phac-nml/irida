import { configureStore } from "@reduxjs/toolkit";
import { userApi } from "../../apis/users/users";
import { settingsApi } from "../../apis/settings/settings";
import { passwordResetApi } from "../../apis/password-reset";
import { projectSubscriptionsApi } from "../../apis/projects/project-subscriptions";
import { sequencingRunsApi } from "../../apis/sequencing-runs/sequencing-runs";

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
    [sequencingRunsApi.reducerPath]: sequencingRunsApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(
      passwordResetApi.middleware,
      projectSubscriptionsApi.middleware,
      userApi.middleware,
      settingsApi.middleware,
      sequencingRunsApi.middleware
    ),
});
