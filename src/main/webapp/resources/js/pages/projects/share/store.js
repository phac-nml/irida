import { configureStore } from "@reduxjs/toolkit";
import { projectsApi } from "../../../apis/projects/projects";
import { samplesApi } from "../../../apis/projects/samples";
import shareReducer from "./shareSlice";

export default configureStore({
  reducer: {
    shareReducer,
    [projectsApi.reducerPath]: projectsApi.reducer,
    [samplesApi.reducerPath]: samplesApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(
      projectsApi.middleware,
      samplesApi.middleware
    ),
});
