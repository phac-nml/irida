import { configureStore } from "@reduxjs/toolkit";
import { sequencingRunsApi } from "../../apis/sequencing-runs/sequencing-runs";
import { runReducer } from "./services/runReducer";
import { projectsApi } from "../../apis/projects/projects";
import { samplesApi } from "../../apis/projects/samples";

/*
Redux Store for sequencing runs.
For more information on redux stores see: https://redux.js.org/tutorials/fundamentals/part-4-store
 */
export default configureStore({
  reducer: {
    reducer: runReducer,
    [sequencingRunsApi.reducerPath]: sequencingRunsApi.reducer,
    [projectsApi.reducerPath]: projectsApi.reducer,
    [samplesApi.reducerPath]: samplesApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(
      sequencingRunsApi.middleware,
      projectsApi.middleware,
      samplesApi.middleware
    ),
});
