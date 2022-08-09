import { configureStore } from "@reduxjs/toolkit";
import { sequencingRunsApi } from "../../apis/sequencing-runs/sequencing-runs";
import { userApi } from "../../apis/users/users";
import { settingsApi } from "../../apis/settings/settings";
import { passwordResetApi } from "../../apis/password-reset";
import { projectSubscriptionsApi } from "../../apis/projects/project-subscriptions";
import { runReducer } from "../sequencing-runs/services/runReducer";
import { projectsApi } from "../../apis/projects/projects";
import { samplesApi } from "../../apis/projects/samples";

/*
Redux Store for admin panel.
For more information on redux stores see: https://redux.js.org/tutorials/fundamentals/part-4-store
 */
export default configureStore({
  reducer: {
    reducer: runReducer,
    [passwordResetApi.reducerPath]: passwordResetApi.reducer,
    [projectSubscriptionsApi.reducerPath]: projectSubscriptionsApi.reducer,
    [userApi.reducerPath]: userApi.reducer,
    [settingsApi.reducerPath]: settingsApi.reducer,
    [sequencingRunsApi.reducerPath]: sequencingRunsApi.reducer,
    [projectsApi.reducerPath]: projectsApi.reducer,
    [samplesApi.reducerPath]: samplesApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(
      passwordResetApi.middleware,
      projectSubscriptionsApi.middleware,
      userApi.middleware,
      settingsApi.middleware,
      sequencingRunsApi.middleware,
      projectsApi.middleware,
      samplesApi.middleware
    ),
});
